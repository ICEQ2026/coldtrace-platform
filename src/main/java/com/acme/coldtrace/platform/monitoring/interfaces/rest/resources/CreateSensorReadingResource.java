package com.acme.coldtrace.platform.monitoring.interfaces.rest.resources;

import java.time.OffsetDateTime;

/**
 * Request resource used to create a sensor reading.
 *
 * @param assetId monitored asset identifier
 * @param iotDeviceId IoT device identifier
 * @param temperature optional temperature value
 * @param humidity optional humidity value
 * @param recordedAt optional reading timestamp
 * @param motionDetected optional motion flag
 * @param imageCaptured optional image capture flag
 * @param batteryLevel optional battery percentage
 * @param signalStrength optional signal strength percentage
 * @since 1.0
 */
public record CreateSensorReadingResource(
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
}
