package com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries;

/**
 * Query for retrieving preventive maintenance schedules owned by an organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetMaintenanceSchedulesByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     *
     * @throws IllegalArgumentException when the organization identifier is missing or invalid
     */
    public GetMaintenanceSchedulesByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("maintenance-management.maintenance-schedule.error.organizationId.invalid");
        }
    }
}
