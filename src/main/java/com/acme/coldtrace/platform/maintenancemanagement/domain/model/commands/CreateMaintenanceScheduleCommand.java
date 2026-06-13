package com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Command for creating a preventive maintenance schedule inside an organization.
 * <p>
 * The command receives the organization identifier from the route and the
 * maintenance scheduling data from the request body. It validates the minimum
 * syntactic invariants before the application layer checks organization and
 * asset ownership through bounded-context facades.
 *
 * @param organizationId organization identifier that owns the schedule
 * @param assetId asset identifier that will receive preventive maintenance
 * @param scheduledDate date and time planned for maintenance execution
 * @param frequencyDays optional recurrence cadence in days
 * @param responsibleUserId optional organization user responsible for the work
 * @param observations optional planning notes
 * @param status initial maintenance schedule status
 * @since 1.0
 */
public record CreateMaintenanceScheduleCommand(
        Long organizationId,
        Long assetId,
        OffsetDateTime scheduledDate,
        Integer frequencyDays,
        Long responsibleUserId,
        String observations,
        String status
) {
    private static final Set<String> SUPPORTED_STATUSES = Set.of("scheduled", "in_progress", "completed", "canceled");

    /**
     * Validates and normalizes maintenance schedule creation data.
     *
     * @throws IllegalArgumentException when identifiers, dates, cadence or status are invalid
     */
    public CreateMaintenanceScheduleCommand {
        organizationId = requirePositive(organizationId, "maintenance-management.maintenance-schedule.error.organizationId.invalid");
        assetId = requirePositive(assetId, "maintenance-management.maintenance-schedule.error.assetId.invalid");
        scheduledDate = requireScheduledDate(scheduledDate);
        frequencyDays = requirePositiveWhenPresent(frequencyDays, "maintenance-management.maintenance-schedule.error.frequencyDays.invalid");
        responsibleUserId = requirePositiveWhenPresent(responsibleUserId, "maintenance-management.maintenance-schedule.error.responsibleUserId.invalid");
        observations = normalizeOptional(observations);
        status = normalizeStatus(status);
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Integer requirePositiveWhenPresent(Integer value, String messageKey) {
        if (value != null && value <= 0) {
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

    private static OffsetDateTime requireScheduledDate(OffsetDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("maintenance-management.maintenance-schedule.error.scheduledDate.required");
        }
        if (value.isBefore(OffsetDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("maintenance-management.maintenance-schedule.error.scheduledDate.invalid");
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

    private static String normalizeOptional(String value) {
        return value == null ? null : value.trim();
    }
}
