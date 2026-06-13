package com.acme.coldtrace.platform.maintenancemanagement.application.queryservices;

/**
 * Failure types for maintenance schedule query execution.
 *
 * @since 1.0
 */
public sealed interface MaintenanceScheduleQueryFailure
        permits MaintenanceScheduleQueryFailure.OrganizationNotFound,
        MaintenanceScheduleQueryFailure.MaintenanceScheduleNotFound {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements MaintenanceScheduleQueryFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.organization-not-found";
        }
    }

    /** Maintenance schedule not found failure. */
    record MaintenanceScheduleNotFound() implements MaintenanceScheduleQueryFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.maintenance-schedule-not-found";
        }
    }
}
