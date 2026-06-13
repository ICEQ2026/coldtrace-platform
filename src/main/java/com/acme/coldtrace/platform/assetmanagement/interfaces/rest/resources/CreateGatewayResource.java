package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request resource for creating a gateway.
 *
 * <p>Gateways represent edge connectivity infrastructure installed in an
 * operational location. This resource validates required identifiers and text
 * at the REST boundary; the application service validates organization
 * ownership, location existence and UUID uniqueness.</p>
 *
 * @param locationId location identifier
 * @param uuid gateway unique identifier
 * @param name gateway name
 * @param network gateway network name
 * @param status gateway status
 * @since 1.0
 */
@Schema(
        name = "CreateGatewayRequest",
        description = "Request payload for creating an organization-scoped edge gateway"
)
public record CreateGatewayResource(
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Organization location where the gateway is installed", example = "1")
        Long locationId,

        @NotBlank(message = "is required")
        @Schema(description = "Gateway business UUID unique inside the organization", example = "GW-LIM-001")
        String uuid,

        @NotBlank(message = "is required")
        @Schema(description = "Human-readable gateway name", example = "Warehouse Gateway 1")
        String name,

        @NotBlank(message = "is required")
        @Schema(description = "Gateway network name or identifier", example = "cold-chain-net")
        String network,

        @NotBlank(message = "is required")
        @Schema(description = "Operational gateway status", example = "online")
        String status
) {
}
