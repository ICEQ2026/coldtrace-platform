package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource for updating a location.
 *
 * <p>The resource carries the replacement state for an existing operational
 * location. Required text is validated before command creation; ownership,
 * existence and duplicate-name checks remain in the application layer.</p>
 *
 * @param name location name
 * @param type location type
 * @param address optional location address
 * @param description optional location description
 * @param status location status
 * @since 1.0
 */
@Schema(
        name = "UpdateLocationRequest",
        description = "Request payload for updating an organization-scoped operational location"
)
public record UpdateLocationResource(
        @NotBlank(message = "is required")
        @Schema(description = "Human-readable location name", example = "Main Warehouse")
        String name,

        @NotBlank(message = "is required")
        @Schema(description = "Operational location type", example = "WAREHOUSE")
        String type,

        @Schema(description = "Optional physical address", example = "Av. Industrial 123, Lima")
        String address,

        @Schema(description = "Optional location notes", example = "Cold storage area for vaccines")
        String description,

        @NotBlank(message = "is required")
        @Schema(description = "Operational location status", example = "active")
        String status
) {
}
