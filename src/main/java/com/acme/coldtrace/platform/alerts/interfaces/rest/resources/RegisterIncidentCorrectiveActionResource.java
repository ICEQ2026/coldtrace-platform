package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to register corrective action on an incident.
 *
 * @param correctiveAction action taken to address the incident cause
 * @param registeredBy actor that registers the corrective action
 * @since 1.0
 */
@Schema(
        name = "RegisterIncidentCorrectiveActionRequest",
        description = "Request payload for registering corrective action on an active incident"
)
public record RegisterIncidentCorrectiveActionResource(
        @NotBlank(message = "is required")
        @Schema(description = "Corrective action applied to the incident", example = "Moved cargo to backup freezer and recalibrated sensor")
        String correctiveAction,

        @NotBlank(message = "is required")
        @Schema(description = "Actor that registers the corrective action", example = "technician@coldtrace.test")
        String registeredBy
) {
}
