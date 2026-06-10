package com.acme.coldtrace.platform.monitoring.domain.model.commands;

import java.time.OffsetDateTime;

/**
 * Command for creating a sensor reading explicitly.
 * <p>
 * The command captures raw telemetry supplied by a client or local simulator.
 * Application services enrich it with gateway, location and threshold evaluation
 * context before the reading is persisted.
 *
 * @param organizationId organization identifier from the route
 * @param assetId monitored asset identifier
 * @param iotDeviceId IoT device that produced the reading
 * @param temperature optional temperature value
 * @param humidity optional humidity value
 * @param recordedAt instant when the device recorded telemetry
 * @param motionDetected optional motion flag
 * @param imageCaptured optional image capture flag
 * @param batteryLevel optional battery percentage
 * @param signalStrength optional signal strength percentage
 * @since 1.0
 */
public record CreateSensorReadingCommand(
        Long organizationId,
        Long assetId,
        Long iotDeviceId,
        Double temperature,
        Double humidity,
        OffsetDateTime recordedAt,
        Boolean motionDetected,
        Boolean imageCaptured,
        Integer batteryLevel,
        Integer signalStrength
) {
    /**
     * Validates and normalizes sensor reading creation data.
     *
     * @throws IllegalArgumentException when required identifiers or telemetry values are invalid
     */
    public CreateSensorReadingCommand {
        organizationId = requirePositive(organizationId, "monitoring.sensor-reading.error.organizationId.invalid");
        assetId = requirePositive(assetId, "monitoring.sensor-reading.error.assetId.invalid");
        iotDeviceId = requirePositive(iotDeviceId, "monitoring.sensor-reading.error.iotDeviceId.invalid");
        recordedAt = recordedAt == null ? OffsetDateTime.now() : recordedAt;
        batteryLevel = requirePercentageWhenPresent(batteryLevel, "monitoring.sensor-reading.error.batteryLevel.invalid");
        signalStrength = requirePercentageWhenPresent(signalStrength, "monitoring.sensor-reading.error.signalStrength.invalid");
        if (temperature == null && humidity == null && motionDetected == null &&
                imageCaptured == null && batteryLevel == null && signalStrength == null) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.telemetry.required");
        }
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Integer requirePercentageWhenPresent(Integer value, String messageKey) {
        if (value != null && (value < 0 || value > 100)) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }
}
