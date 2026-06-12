package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource for creating a location.
 *
 * <p>Locations are organization-scoped operational places where assets and
 * gateways can be installed. This resource validates the required descriptive
 * fields at the REST boundary while the application layer enforces organization
 * existence and duplicate-name rules.</p>
 *
 * @param name location name
 * @param type location type
 * @param address optional location address
 * @param description optional location description
 * @param status location status
 * @since 1.0
 */
@Schema(
        name = "CreateLocationRequest",
        description = "Request payload for creating an organization-scoped operational location"
)
public record CreateLocationResource(
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
