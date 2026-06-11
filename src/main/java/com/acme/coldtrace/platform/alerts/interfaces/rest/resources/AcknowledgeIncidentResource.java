package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to acknowledge an incident.
 *
 * @param acknowledgedBy actor that acknowledges the incident
 * @since 1.0
 */
@Schema(
        name = "AcknowledgeIncidentRequest",
        description = "Request payload for acknowledging an open incident"
)
public record AcknowledgeIncidentResource(
        @NotBlank(message = "is required")
        @Schema(description = "Actor that acknowledges the incident", example = "operations.manager@coldtrace.test")
        String acknowledgedBy
) {
}
