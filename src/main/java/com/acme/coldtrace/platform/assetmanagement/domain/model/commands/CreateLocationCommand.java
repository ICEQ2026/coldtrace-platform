package com.acme.coldtrace.platform.assetmanagement.domain.model.commands;

/**
 * Command for creating a location.
 *
 * @param organizationId organization identifier
 * @param name location name
 * @param type location type
 * @param address optional location address
 * @param description optional location description
 * @param status location status
 * @since 1.0
 */
public record CreateLocationCommand(
        Long organizationId,
        String name,
        String type,
        String address,
        String description,
        String status
) {
    /**
     * Validates and normalizes location creation data.
     *
     * @throws IllegalArgumentException if required fields are blank or identifiers are invalid
     */
    public CreateLocationCommand {
        organizationId = requirePositive(organizationId, "asset-management.location.error.organizationId.invalid");
        name = requireNonBlank(name, "asset-management.location.error.name.required");
        type = requireNonBlank(type, "asset-management.location.error.type.required");
        address = normalizeOptional(address);
        description = normalizeOptional(description);
        status = requireNonBlank(status, "asset-management.location.error.status.required");
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

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
