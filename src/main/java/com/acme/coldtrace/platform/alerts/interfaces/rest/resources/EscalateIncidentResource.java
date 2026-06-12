package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to escalate an incident.
 *
 * @param escalatedBy actor that escalates the incident
 * @param escalationReason business reason for the escalation
 * @since 1.0
 */
@Schema(
        name = "EscalateIncidentRequest",
        description = "Request payload for escalating an active incident"
)
public record EscalateIncidentResource(
        @NotBlank(message = "is required")
        @Schema(description = "Actor that escalates the incident", example = "supervisor@coldtrace.test")
        String escalatedBy,

        @NotBlank(message = "is required")
        @Schema(description = "Reason for escalation", example = "Temperature remains out of range after acknowledgement")
        String escalationReason
) {
}
