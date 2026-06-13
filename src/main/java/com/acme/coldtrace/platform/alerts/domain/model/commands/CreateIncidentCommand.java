package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for manually registering an incident in an organization.
 *
 * @param organizationId organization identifier
 * @param assetId optional asset identifier
 * @param deviceId optional device identifier
 * @param readingId optional sensor reading identifier
 * @param assetName optional asset display name
 * @param deviceName optional device display name
 * @param type incident type
 * @param severity incident severity
 * @param value detected or reported value
 * @since 1.0
 */
public record CreateIncidentCommand(
        Long organizationId,
        Long assetId,
        Long deviceId,
        Long readingId,
        String assetName,
        String deviceName,
        String type,
        String severity,
        String value
) {
    /**
     * Validates and normalizes incident creation data.
     */
    public CreateIncidentCommand {
        organizationId = requirePositive(organizationId, "alerts.incident.error.organizationId.invalid");
        assetId = normalizeOptionalPositive(assetId, "alerts.incident.error.assetId.invalid");
        deviceId = normalizeOptionalPositive(deviceId, "alerts.incident.error.deviceId.invalid");
        readingId = normalizeOptionalPositive(readingId, "alerts.incident.error.readingId.invalid");
        assetName = normalizeOptionalText(assetName);
        deviceName = normalizeOptionalText(deviceName);
        type = requireNonBlank(type, "alerts.incident.error.type.required");
        severity = requireNonBlank(severity, "alerts.incident.error.severity.required").toUpperCase();
        value = normalizeOptionalText(value);
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Long normalizeOptionalPositive(Long value, String messageKey) {
        if (value == null) {
            return null;
        }
        if (value <= 0) {
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
