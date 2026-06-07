package com.acme.coldtrace.platform.assetmanagement.domain.model.commands;

/**
 * Command for updating a gateway.
 *
 * @param organizationId organization identifier
 * @param gatewayId gateway identifier
 * @param locationId location identifier
 * @param uuid gateway unique identifier
 * @param name gateway name
 * @param network gateway network name
 * @param status gateway status
 * @since 1.0
 */
public record UpdateGatewayCommand(
        Long organizationId,
        Long gatewayId,
        Long locationId,
        String uuid,
        String name,
        String network,
        String status
) {
    /**
     * Validates and normalizes gateway update data.
     *
     * @throws IllegalArgumentException if required fields are blank or identifiers are invalid
     */
    public UpdateGatewayCommand {
        organizationId = requirePositive(organizationId, "asset-management.gateway.error.organizationId.invalid");
        gatewayId = requirePositive(gatewayId, "asset-management.gateway.error.gatewayId.invalid");
        locationId = requirePositive(locationId, "asset-management.gateway.error.locationId.invalid");
        uuid = requireNonBlank(uuid, "asset-management.gateway.error.uuid.required");
        name = requireNonBlank(name, "asset-management.gateway.error.name.required");
        network = requireNonBlank(network, "asset-management.gateway.error.network.required");
        status = requireNonBlank(status, "asset-management.gateway.error.status.required");
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
