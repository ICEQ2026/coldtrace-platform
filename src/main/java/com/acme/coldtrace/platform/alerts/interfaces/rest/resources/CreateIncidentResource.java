package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request resource used to manually register an incident.
 *
 * @param assetId optional asset identifier
 * @param deviceId optional device identifier
 * @param readingId optional sensor reading identifier
 * @param assetName optional asset display name
 * @param deviceName optional device display name
 * @param type incident type
 * @param severity incident severity
 * @param value detected or reported value
 * @since 1.0
 */
@Schema(
        name = "CreateIncidentRequest",
        description = "Request payload for manually registering a thermal or operational incident"
)
public record CreateIncidentResource(
        @Positive(message = "must be positive")
        @Schema(description = "Optional asset identifier linked to the incident", example = "1")
        Long assetId,

        @Positive(message = "must be positive")
        @Schema(description = "Optional IoT device identifier linked to the incident", example = "1")
        Long deviceId,

        @Positive(message = "must be positive")
        @Schema(description = "Optional sensor reading identifier that triggered the incident", example = "1")
        Long readingId,

        @Schema(description = "Optional asset display name snapshot", example = "Freezer A1")
        String assetName,

        @Schema(description = "Optional device display name snapshot", example = "Temperature Sensor A1")
        String deviceName,

        @NotBlank(message = "is required")
        @Schema(description = "Incident type", example = "TEMPERATURE_OUT_OF_RANGE")
        String type,

        @NotBlank(message = "is required")
        @Schema(description = "Incident severity", example = "critical")
        String severity,

        @Schema(description = "Detected or reported value", example = "10.5 C")
        String value
) {
}
