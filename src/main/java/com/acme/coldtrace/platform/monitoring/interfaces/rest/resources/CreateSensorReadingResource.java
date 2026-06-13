package com.acme.coldtrace.platform.monitoring.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
@Schema(
        name = "CreateSensorReadingRequest",
        description = "Request payload for recording backend-owned telemetry"
)
public record CreateSensorReadingResource(
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Monitored asset identifier", example = "1")
        Long assetId,

        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "IoT device identifier that produced the reading", example = "1")
        Long iotDeviceId,

        @Schema(description = "Temperature value", example = "5.2")
        Double temperature,

        @Schema(description = "Humidity value", example = "48.0")
        Double humidity,

        @Schema(description = "Reading timestamp; current time is used when omitted", example = "2026-06-10T12:30:00Z")
        OffsetDateTime recordedAt,

        @Schema(description = "Whether motion was detected", example = "false")
        Boolean motionDetected,

        @Schema(description = "Whether an image was captured", example = "false")
        Boolean imageCaptured,

        @Min(value = 0, message = "must be between 0 and 100")
        @Max(value = 100, message = "must be between 0 and 100")
        @Schema(description = "Battery level percentage", example = "92")
        Integer batteryLevel,

        @Min(value = 0, message = "must be between 0 and 100")
        @Max(value = 100, message = "must be between 0 and 100")
        @Schema(description = "Signal strength percentage", example = "87")
        Integer signalStrength
) {
}
