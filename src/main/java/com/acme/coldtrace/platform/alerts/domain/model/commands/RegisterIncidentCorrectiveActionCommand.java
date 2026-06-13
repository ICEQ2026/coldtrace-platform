package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for registering corrective action details on an active incident.
 *
 * @param organizationId organization identifier
 * @param incidentId incident identifier
 * @param correctiveAction action taken to address the incident cause
 * @param registeredBy actor that registered the corrective action
 * @since 1.0
 */
public record RegisterIncidentCorrectiveActionCommand(
        Long organizationId,
        Long incidentId,
        String correctiveAction,
        String registeredBy
) {
    /**
     * Validates and normalizes corrective action data.
     */
    public RegisterIncidentCorrectiveActionCommand {
        organizationId = requirePositive(organizationId, "alerts.incident.error.organizationId.invalid");
        incidentId = requirePositive(incidentId, "alerts.incident.error.incidentId.invalid");
        correctiveAction = requireNonBlank(correctiveAction, "alerts.incident.error.correctiveAction.required");
        registeredBy = requireNonBlank(registeredBy, "alerts.incident.error.correctiveActionRegisteredBy.required");
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
