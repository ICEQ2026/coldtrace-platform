package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.SaveAssetSettingsCommand;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Asset settings aggregate for the asset management bounded context.
 * <p>
 * Asset settings define the safety thresholds and operational telemetry
 * parameters that later bounded contexts use to evaluate sensor readings and
 * raise incidents. A settings record can target one specific asset or work as
 * the organization default when {@code assetId} is not present.
 * <p>
 * The aggregate stores organization and asset identifiers instead of direct
 * object references. Ownership validation is performed by the application
 * service before persistence so the domain model remains independent from JPA
 * and other aggregate repositories.
 *
 * @since 1.0
 */
@Getter
public class AssetSettings extends AbstractDomainAggregateRoot<AssetSettings> {
    private Long id;
    private Long organizationId;
    private Long assetId;
    private String uuid;
    private List<String> assetTypes;
    private List<String> iotDeviceTypes;
    private Double minimumTemperature;
    private Double maximumTemperature;
    private Double minimumHumidity;
    private Double maximumHumidity;
    private Integer calibrationFrequencyDays;
    private String temperatureUnit;
    private String humidityUnit;
    private String weightUnit;
    private Integer readingFrequencySeconds;
    private Integer alertThresholdMinutes;

    protected AssetSettings() {
    }

    /**
     * Creates settings from a validated save command.
     *
     * @param command command containing organization, optional asset and threshold data
     * @see SaveAssetSettingsCommand
     */
    public AssetSettings(SaveAssetSettingsCommand command) {
        this.organizationId = command.organizationId();
        this.assetId = command.assetId();
        this.uuid = normalizeUuid(command.uuid());
        this.assetTypes = List.copyOf(command.assetTypes());
        this.iotDeviceTypes = List.copyOf(command.iotDeviceTypes());
        this.minimumTemperature = command.minimumTemperature();
        this.maximumTemperature = command.maximumTemperature();
        this.minimumHumidity = command.minimumHumidity();
        this.maximumHumidity = command.maximumHumidity();
        this.calibrationFrequencyDays = command.calibrationFrequencyDays();
        this.temperatureUnit = command.temperatureUnit();
        this.humidityUnit = command.humidityUnit();
        this.weightUnit = command.weightUnit();
        this.readingFrequencySeconds = command.readingFrequencySeconds();
        this.alertThresholdMinutes = command.alertThresholdMinutes();
    }

    /**
     * Rebuilds settings from persistence state.
     *
     * @param id persistence identifier
     * @param organizationId organization that owns the settings
     * @param assetId optional asset-specific target
     * @param uuid external settings code
     * @param assetTypes asset types where the settings apply
     * @param iotDeviceTypes device types supported by the settings
     * @param minimumTemperature lower safe temperature
     * @param maximumTemperature upper safe temperature
     * @param minimumHumidity lower safe humidity
     * @param maximumHumidity upper safe humidity
     * @param calibrationFrequencyDays calibration cadence in days
     * @param temperatureUnit temperature unit label
     * @param humidityUnit humidity unit label
     * @param weightUnit weight unit label
     * @param readingFrequencySeconds telemetry cadence in seconds
     * @param alertThresholdMinutes alert delay threshold in minutes
     */
    public AssetSettings(
            Long id,
            Long organizationId,
            Long assetId,
            String uuid,
            List<String> assetTypes,
            List<String> iotDeviceTypes,
            Double minimumTemperature,
            Double maximumTemperature,
            Double minimumHumidity,
            Double maximumHumidity,
            Integer calibrationFrequencyDays,
            String temperatureUnit,
            String humidityUnit,
            String weightUnit,
            Integer readingFrequencySeconds,
            Integer alertThresholdMinutes
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.uuid = uuid;
        this.assetTypes = List.copyOf(assetTypes);
        this.iotDeviceTypes = List.copyOf(iotDeviceTypes);
        this.minimumTemperature = minimumTemperature;
        this.maximumTemperature = maximumTemperature;
        this.minimumHumidity = minimumHumidity;
        this.maximumHumidity = maximumHumidity;
        this.calibrationFrequencyDays = calibrationFrequencyDays;
        this.temperatureUnit = temperatureUnit;
        this.humidityUnit = humidityUnit;
        this.weightUnit = weightUnit;
        this.readingFrequencySeconds = readingFrequencySeconds;
        this.alertThresholdMinutes = alertThresholdMinutes;
    }

    /**
     * Updates threshold and operational values from a save command.
     * <p>
     * The organization and asset references remain anchored to the route scope.
     * The external uuid is preserved when the command does not provide one, so
     * an idempotent PUT does not accidentally rotate existing identifiers.
     *
     * @param command command containing the new settings state
     * @see SaveAssetSettingsCommand
     */
    public void update(SaveAssetSettingsCommand command) {
        this.uuid = command.uuid() == null || command.uuid().isBlank()
                ? this.uuid
                : command.uuid().trim();
        this.assetTypes = List.copyOf(command.assetTypes());
        this.iotDeviceTypes = List.copyOf(command.iotDeviceTypes());
        this.minimumTemperature = command.minimumTemperature();
        this.maximumTemperature = command.maximumTemperature();
        this.minimumHumidity = command.minimumHumidity();
        this.maximumHumidity = command.maximumHumidity();
        this.calibrationFrequencyDays = command.calibrationFrequencyDays();
        this.temperatureUnit = command.temperatureUnit();
        this.humidityUnit = command.humidityUnit();
        this.weightUnit = command.weightUnit();
        this.readingFrequencySeconds = command.readingFrequencySeconds();
        this.alertThresholdMinutes = command.alertThresholdMinutes();
    }

    private static String normalizeUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            return "AST-SET-" + UUID.randomUUID();
        }
        return uuid.trim();
    }
}
