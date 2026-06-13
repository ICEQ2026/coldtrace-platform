package com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands;

import java.util.Set;

/**
 * Command for updating the lifecycle status of a technical service request.
 *
 * @param organizationId organization identifier from the route
 * @param technicalServiceRequestId technical service request identifier from the route
 * @param status requested lifecycle status
 * @param closureSummary closure summary required when closing the request
 * @param evidence closure evidence required when closing the request
 * @param closedBy actor closing the request
 * @since 1.0
 */
public record UpdateTechnicalServiceRequestStatusCommand(
        Long organizationId,
        Long technicalServiceRequestId,
        String status,
        String closureSummary,
        String evidence,
        String closedBy
) {
    private static final Set<String> SUPPORTED_STATUSES = Set.of("open", "in_progress", "closed", "canceled");

    /**
     * Validates and normalizes technical service request status update data.
     *
     * @throws IllegalArgumentException when identifiers or status are invalid
     */
    public UpdateTechnicalServiceRequestStatusCommand {
        organizationId = requirePositive(organizationId, "maintenance-management.technical-service-request.error.organizationId.invalid");
        technicalServiceRequestId = requirePositive(
                technicalServiceRequestId,
                "maintenance-management.technical-service-request.error.requestId.invalid"
        );
        status = normalizeStatus(status);
        closureSummary = normalizeOptional(closureSummary);
        evidence = normalizeOptional(evidence);
        closedBy = normalizeOptional(closedBy);
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static String normalizeStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("maintenance-management.technical-service-request.error.status.required");
        }
        var normalized = value.trim().toLowerCase();
        if (!SUPPORTED_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("maintenance-management.technical-service-request.error.status.invalid");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        return value == null ? null : value.trim();
    }
}
