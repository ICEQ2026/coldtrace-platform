package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to update a preventive maintenance schedule status.
 *
 * @param status requested lifecycle status
 * @since 1.0
 */
@Schema(
        name = "UpdateMaintenanceScheduleStatusRequest",
        description = "Request payload for changing the lifecycle status of a maintenance schedule"
)
public record UpdateMaintenanceScheduleStatusResource(
        @NotBlank(message = "is required")
        @Schema(description = "Requested lifecycle status", example = "completed")
        String status
) {
}
