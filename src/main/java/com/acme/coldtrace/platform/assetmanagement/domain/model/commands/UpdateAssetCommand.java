package com.acme.coldtrace.platform.assetmanagement.domain.model.commands;

/**
 * Command for updating an asset inside an organization.
 * <p>
 * The command keeps the organization and asset identifiers explicit so the
 * application service can verify that the target aggregate belongs to the
 * route-selected organization before applying mutations.
 *
 * @param organizationId organization identifier that scopes the asset
 * @param assetId asset identifier to update
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
public record UpdateAssetCommand(
        Long organizationId,
        Long assetId,
        Long locationId,
        String uuid,
        String type,
        String name,
        Double capacity,
        String description,
        String status
) {
    /**
     * Validates and normalizes asset update data.
     *
     * @throws IllegalArgumentException if required fields are blank, identifiers
     *                                  are invalid or capacity is not positive
     */
    public UpdateAssetCommand {
        organizationId = requirePositive(organizationId, "asset-management.asset.error.organizationId.invalid");
        assetId = requirePositive(assetId, "asset-management.asset.error.assetId.invalid");
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
