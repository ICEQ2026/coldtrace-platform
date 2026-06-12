package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for resolving an incident.
 *
 * @param organizationId organization identifier
 * @param incidentId incident identifier
 * @param resolvedBy actor that resolved the incident
 * @param resolutionNotes resolution notes
 * @since 1.0
 */
public record ResolveIncidentCommand(
        Long organizationId,
        Long incidentId,
        String resolvedBy,
        String resolutionNotes
) {
    /**
     * Validates and normalizes resolution data.
     */
    public ResolveIncidentCommand {
        organizationId = requirePositive(organizationId, "alerts.incident.error.organizationId.invalid");
        incidentId = requirePositive(incidentId, "alerts.incident.error.incidentId.invalid");
        resolvedBy = requireNonBlank(resolvedBy, "alerts.incident.error.resolvedBy.required");
        resolutionNotes = normalizeOptionalText(resolutionNotes);
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

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
