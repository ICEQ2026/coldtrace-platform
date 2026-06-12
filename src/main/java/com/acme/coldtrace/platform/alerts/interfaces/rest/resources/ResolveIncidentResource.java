package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to resolve an incident.
 *
 * @param resolvedBy actor that resolves the incident
 * @param resolutionNotes resolution notes
 * @since 1.0
 */
@Schema(
        name = "ResolveIncidentRequest",
        description = "Request payload for resolving an incident with corrective action notes"
)
public record ResolveIncidentResource(
        @NotBlank(message = "is required")
        @Schema(description = "Actor that resolves the incident", example = "operations.manager@coldtrace.test")
        String resolvedBy,

        @NotBlank(message = "is required")
        @Schema(description = "Corrective action or resolution notes", example = "Moved inventory and recalibrated the freezer.")
        String resolutionNotes
) {
}
