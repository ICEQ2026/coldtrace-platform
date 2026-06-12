package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for escalating an active incident.
 *
 * @param organizationId organization identifier
 * @param incidentId incident identifier
 * @param escalatedBy actor that escalates the incident
 * @param escalationReason business reason for the escalation
 * @since 1.0
 */
public record EscalateIncidentCommand(
        Long organizationId,
        Long incidentId,
        String escalatedBy,
        String escalationReason
) {
    /**
     * Validates and normalizes escalation data.
     */
    public EscalateIncidentCommand {
        organizationId = requirePositive(organizationId, "alerts.incident.error.organizationId.invalid");
        incidentId = requirePositive(incidentId, "alerts.incident.error.incidentId.invalid");
        escalatedBy = requireNonBlank(escalatedBy, "alerts.incident.error.escalatedBy.required");
        escalationReason = requireNonBlank(escalationReason, "alerts.incident.error.escalationReason.required");
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
