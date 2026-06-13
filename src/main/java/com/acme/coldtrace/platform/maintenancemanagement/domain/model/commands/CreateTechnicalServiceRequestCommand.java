package com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands;

/**
 * Command for creating a corrective technical service request.
 * <p>
 * The command captures the route organization identifier and request body data
 * before the application layer verifies organization, asset and optional
 * incident ownership through ACL facades.
 *
 * @param organizationId organization identifier that owns the request
 * @param assetId asset that requires corrective service
 * @param incidentId optional related incident identifier
 * @param issueDescription problem description reported by operations
 * @param priority service priority
 * @param requestedBy optional requester name or email
 * @since 1.0
 */
public record CreateTechnicalServiceRequestCommand(
        Long organizationId,
        Long assetId,
        Long incidentId,
        String issueDescription,
        String priority,
        String requestedBy
) {
    /**
     * Validates and normalizes technical service request creation data.
     *
     * @throws IllegalArgumentException when identifiers or required text fields are invalid
     */
    public CreateTechnicalServiceRequestCommand {
        organizationId = requirePositive(organizationId, "maintenance-management.technical-service-request.error.organizationId.invalid");
        assetId = requirePositive(assetId, "maintenance-management.technical-service-request.error.assetId.invalid");
        incidentId = requirePositiveWhenPresent(incidentId, "maintenance-management.technical-service-request.error.incidentId.invalid");
        issueDescription = requireNonBlank(
                issueDescription,
                "maintenance-management.technical-service-request.error.issueDescription.required"
        );
        priority = requireNonBlank(priority, "maintenance-management.technical-service-request.error.priority.required");
        requestedBy = normalizeOptional(requestedBy);
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Long requirePositiveWhenPresent(Long value, String messageKey) {
        if (value != null && value <= 0) {
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
