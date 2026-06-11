package com.acme.coldtrace.platform.maintenancemanagement.application.commandservices;

/**
 * Failure types produced while executing maintenance schedule command use cases.
 * <p>
 * Failures are explicit so the REST layer can map every business rejection to
 * a deterministic status code and i18n message without parsing exception text.
 *
 * @since 1.0
 */
public sealed interface MaintenanceScheduleCommandFailure
        permits MaintenanceScheduleCommandFailure.OrganizationNotFound,
        MaintenanceScheduleCommandFailure.AssetNotFound,
        MaintenanceScheduleCommandFailure.MaintenanceScheduleNotFound,
        MaintenanceScheduleCommandFailure.DuplicateActiveSchedule,
        MaintenanceScheduleCommandFailure.InvalidStatusTransition {
    /**
     * Returns the message key associated with the failure.
     *
     * @return message key to resolve through i18n
     */
    String messageKey();

    /**
     * Returns optional arguments for message interpolation.
     *
     * @return message interpolation arguments
     */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements MaintenanceScheduleCommandFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.organization-not-found";
        }
    }

    /** Asset not found or not owned by the organization failure. */
    record AssetNotFound() implements MaintenanceScheduleCommandFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.asset-not-found";
        }
    }

    /** Maintenance schedule not found failure. */
    record MaintenanceScheduleNotFound() implements MaintenanceScheduleCommandFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.maintenance-schedule-not-found";
        }
    }

    /** Duplicate active maintenance schedule for the same asset failure. */
    record DuplicateActiveSchedule() implements MaintenanceScheduleCommandFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.duplicate-active-schedule";
        }
    }

    /** Invalid lifecycle status transition failure. */
    record InvalidStatusTransition() implements MaintenanceScheduleCommandFailure {
        @Override
        public String messageKey() {
            return "maintenance-management.maintenance-schedule.error.invalid-status-transition";
        }
    }
}
