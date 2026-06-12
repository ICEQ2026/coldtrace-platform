package com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands;

import java.util.Set;

/**
 * Command for updating the lifecycle status of a preventive maintenance schedule.
 *
 * @param organizationId organization identifier from the route
 * @param maintenanceScheduleId maintenance schedule identifier from the route
 * @param status requested lifecycle status
 * @since 1.0
 */
public record UpdateMaintenanceScheduleStatusCommand(
        Long organizationId,
        Long maintenanceScheduleId,
        String status
) {
    private static final Set<String> SUPPORTED_STATUSES = Set.of("scheduled", "in_progress", "completed", "canceled");

    /**
     * Validates and normalizes the status update command.
     *
     * @throws IllegalArgumentException when identifiers or status are invalid
     */
    public UpdateMaintenanceScheduleStatusCommand {
        organizationId = requirePositive(organizationId, "maintenance-management.maintenance-schedule.error.organizationId.invalid");
        maintenanceScheduleId = requirePositive(
                maintenanceScheduleId,
                "maintenance-management.maintenance-schedule.error.maintenanceScheduleId.invalid"
        );
        status = normalizeStatus(status);
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static String normalizeStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("maintenance-management.maintenance-schedule.error.status.required");
        }
        var normalized = value.trim().toLowerCase();
        if (!SUPPORTED_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("maintenance-management.maintenance-schedule.error.status.invalid");
        }
        return normalized;
    }
}
