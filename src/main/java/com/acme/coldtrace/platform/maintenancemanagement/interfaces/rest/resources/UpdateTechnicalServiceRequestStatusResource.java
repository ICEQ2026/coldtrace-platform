package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to update a technical service request lifecycle status.
 *
 * @param status requested service status
 * @param closureSummary closure summary required when status is closed
 * @param evidence closure evidence required when status is closed
 * @param closedBy actor closing the request
 * @since 1.0
 */
@Schema(
        name = "UpdateTechnicalServiceRequestStatus",
        description = "Request payload for service status updates and closure"
)
public record UpdateTechnicalServiceRequestStatusResource(
        @NotBlank(message = "is required")
        @Schema(description = "Requested service status", example = "closed")
        String status,

        @Schema(description = "Closure summary required when status is closed", example = "Replaced relay and verified stable cooling")
        String closureSummary,

        @Schema(description = "Evidence required when status is closed", example = "Photo reference or technician notes")
        String evidence,

        @Schema(description = "Actor closing the request", example = "technician@coldtrace.test")
        String closedBy
) {
}
