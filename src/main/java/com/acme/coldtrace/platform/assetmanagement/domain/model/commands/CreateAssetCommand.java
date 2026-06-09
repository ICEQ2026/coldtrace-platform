package com.acme.coldtrace.platform.assetmanagement.domain.model.commands;

/**
 * Command for creating an asset inside an organization.
 * <p>
 * The command receives the organization identifier from the route and the
 * remaining asset data from the request body. Its compact constructor validates
 * the minimum invariants required before the application layer coordinates
 * persistence and cross-aggregate checks.
 *
 * @param organizationId organization identifier that scopes the asset
 * @param locationId location identifier where the asset is placed
 * @param uuid asset unique identifier inside the organization
 * @param type business asset type, for example cold-room or transport
 * @param name asset display name
 * @param capacity asset capacity expressed with the business unit selected by
 *                 the client application
 * @param description optional asset description
 * @param status asset operational status
 * @since 1.0
 */
public record CreateAssetCommand(
        Long organizationId,
        Long locationId,
        String uuid,
        String type,
        String name,
        Double capacity,
        String description,
        String status
) {
    /**
     * Validates and normalizes asset creation data.
     *
     * @throws IllegalArgumentException if required fields are blank, identifiers
     *                                  are invalid or capacity is not positive
     */
    public CreateAssetCommand {
        organizationId = requirePositive(organizationId, "asset-management.asset.error.organizationId.invalid");
        locationId = requirePositive(locationId, "asset-management.asset.error.locationId.invalid");
        uuid = requireNonBlank(uuid, "asset-management.asset.error.uuid.required");
        type = requireNonBlank(type, "asset-management.asset.error.type.required");
        name = requireNonBlank(name, "asset-management.asset.error.name.required");
        capacity = requirePositive(capacity, "asset-management.asset.error.capacity.invalid");
        description = normalizeOptional(description);
        status = requireNonBlank(status, "asset-management.asset.error.status.required");
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Double requirePositive(Double value, String messageKey) {
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

    private static String normalizeOptional(String value) {
        return value == null ? null : value.trim();
    }
}
