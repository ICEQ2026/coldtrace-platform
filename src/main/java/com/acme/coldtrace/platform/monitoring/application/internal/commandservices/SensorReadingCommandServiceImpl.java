package com.acme.coldtrace.platform.monitoring.application.internal.commandservices;

import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.AssetSettingsSnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.AssetSnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.GatewaySnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.IoTDeviceSnapshot;
import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.monitoring.application.commandservices.SensorReadingCommandFailure;
import com.acme.coldtrace.platform.monitoring.application.commandservices.SensorReadingCommandService;
import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.model.commands.CreateSensorReadingCommand;
import com.acme.coldtrace.platform.monitoring.domain.model.commands.GenerateDemoSensorReadingsCommand;
import com.acme.coldtrace.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Application service implementation for sensor reading command operations.
 * <p>
 * The service owns the monitoring rules that used to be simulated by the
 * frontend: device/gateway availability, assignment validation, effective asset
 * settings lookup and out-of-range evaluation. Each persisted reading captures
 * asset, device, gateway and location context for later reporting.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class SensorReadingCommandServiceImpl implements SensorReadingCommandService {
    private static final String OFFLINE_STATUS = "offline";
    private static final int LOW_BATTERY_THRESHOLD = 15;
    private static final int LOW_SIGNAL_THRESHOLD = 35;

    private final SensorReadingRepository sensorReadingRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final Random random = new Random();

    public SensorReadingCommandServiceImpl(
            SensorReadingRepository sensorReadingRepository,
            IdentityAccessContextFacade identityAccessContextFacade,
            AssetManagementContextFacade assetManagementContextFacade
    ) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
    }

    /**
     * Handles explicit creation of a sensor reading.
     *
     * @param command command containing raw telemetry
     * @return success with created reading or failure with command error
     * @see CreateSensorReadingCommand
     */
    @Override
    @Transactional
    public Result<SensorReading, SensorReadingCommandFailure> handle(CreateSensorReadingCommand command) {
        var contextResult = resolveContext(command.organizationId(), command.assetId(), command.iotDeviceId());
        if (contextResult.isFailure()) {
            return Result.failure(contextResult.failure().orElseThrow());
        }
        var context = contextResult.success().orElseThrow();
        if (!supportsRequestedMeasurements(context.device(), command.temperature(), command.humidity(),
                command.motionDetected(), command.imageCaptured(), command.batteryLevel(), command.signalStrength())) {
            return Result.failure(new SensorReadingCommandFailure.UnsupportedMeasurement());
        }

        var outOfRange = evaluateOutOfRange(
                context.settings(),
                command.temperature(),
                command.humidity(),
                command.batteryLevel(),
                command.signalStrength()
        );
        var reading = new SensorReading(
                command.organizationId(),
                command.assetId(),
                command.iotDeviceId(),
                context.gateway().id(),
                context.asset().locationId(),
                command.temperature(),
                command.humidity(),
                outOfRange,
                command.recordedAt(),
                command.motionDetected(),
                command.imageCaptured(),
                command.batteryLevel(),
                command.signalStrength()
        );
        var saved = sensorReadingRepository.save(reading);
        log.info("Sensor reading created: id={}, organizationId={}, assetId={}, iotDeviceId={}",
                saved.getId(), saved.getOrganizationId(), saved.getAssetId(), saved.getIotDeviceId());
        return Result.success(saved);
    }

    /**
     * Handles backend-owned demo generation of sensor readings.
     *
     * @param command command containing generation scope and count
     * @return success with generated readings or failure with command error
     * @see GenerateDemoSensorReadingsCommand
     */
    @Override
    @Transactional
    public Result<List<SensorReading>, SensorReadingCommandFailure> handle(GenerateDemoSensorReadingsCommand command) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            return Result.failure(new SensorReadingCommandFailure.OrganizationNotFound());
        }
        if (command.assetId() != null &&
                assetManagementContextFacade.fetchAssetByIdAndOrganizationId(
                        command.organizationId(),
                        command.assetId()
                ).isEmpty()) {
            return Result.failure(new SensorReadingCommandFailure.AssetNotFound());
        }

        var candidates = assetManagementContextFacade
                .fetchAssignedIoTDevices(command.organizationId(), command.assetId())
                .stream()
                .filter(device -> !OFFLINE_STATUS.equalsIgnoreCase(device.status()))
                .toList();
        var generated = new ArrayList<SensorReading>();
        for (var index = 0; index < command.count(); index++) {
            var reading = generateOne(command.organizationId(), candidates, index);
            reading.ifPresent(generated::add);
        }
        if (generated.isEmpty()) {
            return Result.failure(new SensorReadingCommandFailure.NoEligibleDevices());
        }
        return Result.success(generated);
    }

    private Optional<SensorReading> generateOne(Long organizationId, List<IoTDeviceSnapshot> candidates, int offsetMinutes) {
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        var start = random.nextInt(candidates.size());
        for (int attempt = 0; attempt < candidates.size(); attempt++) {
            var device = candidates.get((start + attempt) % candidates.size());
            var context = resolveContext(organizationId, device.assetId(), device.id());
            if (context.isFailure()) {
                continue;
            }
            var current = context.success().orElseThrow();
            var parameters = current.device().measurementParameters();
            var temperature = parameters.contains("temperature")
                    ? randomTemperature(current.settings().minimumTemperature(), current.settings().maximumTemperature())
                    : null;
            var humidity = parameters.contains("humidity")
                    ? randomHumidity(current.settings().minimumHumidity(), current.settings().maximumHumidity())
                    : null;
            var motionDetected = parameters.contains("motion") ? random.nextDouble() < 0.18 : null;
            var imageCaptured = parameters.contains("image") ? random.nextDouble() < 0.35 : null;
            var batteryLevel = parameters.contains("battery") ? randomPercentage(8, 100, 0.96, 20, 100) : null;
            var signalStrength = parameters.contains("signal") ? randomPercentage(28, 100, 0.96, 40, 100) : null;
            var outOfRange = evaluateOutOfRange(current.settings(), temperature, humidity, batteryLevel, signalStrength);
            var saved = sensorReadingRepository.save(new SensorReading(
                    organizationId,
                    current.asset().id(),
                    current.device().id(),
                    current.gateway().id(),
                    current.asset().locationId(),
                    temperature,
                    humidity,
                    outOfRange,
                    OffsetDateTime.now().minusMinutes(offsetMinutes),
                    motionDetected,
                    imageCaptured,
                    batteryLevel,
                    signalStrength
            ));
            return Optional.of(saved);
        }
        return Optional.empty();
    }

    private Result<ReadingContext, SensorReadingCommandFailure> resolveContext(
            Long organizationId,
            Long assetId,
            Long iotDeviceId
    ) {
        if (!identityAccessContextFacade.organizationExists(organizationId)) {
            return Result.failure(new SensorReadingCommandFailure.OrganizationNotFound());
        }
        var asset = assetManagementContextFacade.fetchAssetByIdAndOrganizationId(organizationId, assetId);
        if (asset.isEmpty()) {
            return Result.failure(new SensorReadingCommandFailure.AssetNotFound());
        }
        var device = assetManagementContextFacade.fetchIoTDeviceByIdAndOrganizationId(organizationId, iotDeviceId);
        if (device.isEmpty()) {
            return Result.failure(new SensorReadingCommandFailure.IoTDeviceNotFound());
        }
        if (!assetId.equals(device.get().assetId())) {
            return Result.failure(new SensorReadingCommandFailure.DeviceNotAssignedToAsset());
        }
        if (OFFLINE_STATUS.equalsIgnoreCase(device.get().status())) {
            return Result.failure(new SensorReadingCommandFailure.DeviceOffline());
        }
        var gateway = assetManagementContextFacade.fetchGatewayByIdAndOrganizationId(
                organizationId,
                device.get().gatewayId()
        );
        if (gateway.isEmpty()) {
            return Result.failure(new SensorReadingCommandFailure.GatewayNotFound());
        }
        if (OFFLINE_STATUS.equalsIgnoreCase(gateway.get().status())) {
            return Result.failure(new SensorReadingCommandFailure.GatewayOffline());
        }
        if (!asset.get().locationId().equals(gateway.get().locationId())) {
            return Result.failure(new SensorReadingCommandFailure.IncompatibleLocation());
        }
        var settings = assetManagementContextFacade.fetchEffectiveAssetSettingsByAssetId(organizationId, assetId);
        if (settings.isEmpty()) {
            return Result.failure(new SensorReadingCommandFailure.AssetSettingsNotFound());
        }
        return Result.success(new ReadingContext(asset.get(), device.get(), gateway.get(), settings.get()));
    }

    private boolean supportsRequestedMeasurements(
            IoTDeviceSnapshot device,
            Double temperature,
            Double humidity,
            Boolean motionDetected,
            Boolean imageCaptured,
            Integer batteryLevel,
            Integer signalStrength
    ) {
        var parameters = device.measurementParameters();
        return (temperature == null || parameters.contains("temperature")) &&
                (humidity == null || parameters.contains("humidity")) &&
                (motionDetected == null || parameters.contains("motion")) &&
                (imageCaptured == null || parameters.contains("image")) &&
                (batteryLevel == null || parameters.contains("battery")) &&
                (signalStrength == null || parameters.contains("signal"));
    }

    private boolean evaluateOutOfRange(
            AssetSettingsSnapshot settings,
            Double temperature,
            Double humidity,
            Integer batteryLevel,
            Integer signalStrength
    ) {
        var temperatureOutOfRange = temperature != null &&
                (temperature < settings.minimumTemperature() || temperature > settings.maximumTemperature());
        var humidityOutOfRange = humidity != null &&
                (humidity < settings.minimumHumidity() || humidity > settings.maximumHumidity());
        var batteryOutOfRange = batteryLevel != null && batteryLevel < LOW_BATTERY_THRESHOLD;
        var signalOutOfRange = signalStrength != null && signalStrength < LOW_SIGNAL_THRESHOLD;
        return temperatureOutOfRange || humidityOutOfRange || batteryOutOfRange || signalOutOfRange;
    }

    private Double randomTemperature(Double minimum, Double maximum) {
        var roll = random.nextDouble();
        if (roll < 0.94) {
            return roundOne(randomDouble(minimum, maximum));
        }
        if (roll < 0.97) {
            return roundOne(randomDouble(minimum - 2, minimum - 0.2));
        }
        return roundOne(randomDouble(maximum + 0.2, maximum + 3));
    }

    private Double randomHumidity(Double minimum, Double maximum) {
        var roll = random.nextDouble();
        if (roll < 0.94) {
            return roundOne(randomDouble(minimum, maximum));
        }
        return roundOne(randomDouble(maximum + 1, maximum + 8));
    }

    private Integer randomPercentage(int abnormalMinimum, int abnormalMaximum, double normalChance,
                                     int normalMinimum, int normalMaximum) {
        if (random.nextDouble() < normalChance) {
            return random.nextInt(normalMaximum - normalMinimum + 1) + normalMinimum;
        }
        return random.nextInt(abnormalMaximum - abnormalMinimum + 1) + abnormalMinimum;
    }

    private Double randomDouble(Double minimum, Double maximum) {
        return minimum + random.nextDouble() * (maximum - minimum);
    }

    private Double roundOne(Double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record ReadingContext(
            AssetSnapshot asset,
            IoTDeviceSnapshot device,
            GatewaySnapshot gateway,
            AssetSettingsSnapshot settings
    ) {
    }
}
