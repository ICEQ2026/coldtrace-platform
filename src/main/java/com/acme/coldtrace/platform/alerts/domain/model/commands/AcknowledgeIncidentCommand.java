package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for acknowledging an open incident.
 *
 * @param organizationId organization identifier
 * @param incidentId incident identifier
 * @param acknowledgedBy actor that acknowledged the incident
 * @since 1.0
 */
public record AcknowledgeIncidentCommand(
        Long organizationId,
        Long incidentId,
        String acknowledgedBy
) {
    /**
     * Validates and normalizes acknowledgement data.
     */
    public AcknowledgeIncidentCommand {
        organizationId = requirePositive(organizationId, "alerts.incident.error.organizationId.invalid");
        incidentId = requirePositive(incidentId, "alerts.incident.error.incidentId.invalid");
        acknowledgedBy = requireNonBlank(acknowledgedBy, "alerts.incident.error.acknowledgedBy.required");
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
