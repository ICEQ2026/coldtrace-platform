package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request resource used to open a corrective technical service request.
 *
 * @param assetId asset that requires corrective service
 * @param incidentId optional related incident identifier
 * @param issueDescription reported problem description
 * @param priority service priority
 * @param requestedBy optional requester name or email
 * @since 1.0
 */
@Schema(
        name = "CreateTechnicalServiceRequest",
        description = "Request payload for opening corrective technical service"
)
public record CreateTechnicalServiceRequestResource(
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Maintained asset identifier", example = "1")
        Long assetId,

        @Positive(message = "must be positive")
        @Schema(description = "Optional related incident identifier", example = "3")
        Long incidentId,

        @NotBlank(message = "is required")
        @Schema(description = "Issue description", example = "Compressor does not keep target temperature")
        String issueDescription,

        @NotBlank(message = "is required")
        @Schema(description = "Service priority", example = "high")
        String priority,

        @Schema(description = "Requester name or email", example = "operator@coldtrace.test")
        String requestedBy
) {
}
