package com.acme.coldtrace.platform.monitoring.interfaces.events;

import java.time.OffsetDateTime;

/**
 * Integration event published when monitoring records telemetry.
 *
 * @param sensorReadingId sensor reading identifier
 * @param organizationId owning organization identifier
 * @param assetId asset identifier
 * @param iotDeviceId IoT device identifier
 * @param outOfRange whether the reading is outside configured thresholds
 * @param recordedAt reading timestamp
 * @since 1.0
 */
public record SensorReadingRecordedIntegrationEvent(
        Long sensorReadingId,
        Long organizationId,
        Long assetId,
        Long iotDeviceId,
        Boolean outOfRange,
        OffsetDateTime recordedAt
) {
}
