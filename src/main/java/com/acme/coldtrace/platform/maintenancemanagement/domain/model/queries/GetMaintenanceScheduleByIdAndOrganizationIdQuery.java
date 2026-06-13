package com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries;

/**
 * Query for retrieving one preventive maintenance schedule by id and organization.
 *
 * @param organizationId organization identifier
 * @param maintenanceScheduleId maintenance schedule identifier
 * @since 1.0
 */
public record GetMaintenanceScheduleByIdAndOrganizationIdQuery(Long organizationId, Long maintenanceScheduleId) {
    /**
     * Validates query identifiers.
     *
     * @throws IllegalArgumentException when identifiers are missing or invalid
     */
    public GetMaintenanceScheduleByIdAndOrganizationIdQuery {
        organizationId = requirePositive(organizationId, "maintenance-management.maintenance-schedule.error.organizationId.invalid");
        maintenanceScheduleId = requirePositive(
                maintenanceScheduleId,
                "maintenance-management.maintenance-schedule.error.maintenanceScheduleId.invalid"
        );
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }
}
