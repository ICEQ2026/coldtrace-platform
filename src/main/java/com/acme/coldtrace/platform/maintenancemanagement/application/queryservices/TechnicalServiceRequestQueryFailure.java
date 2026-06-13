package com.acme.coldtrace.platform.maintenancemanagement.application.queryservices;

/**
 * Failure types for technical service request query execution.
 *
 * @since 1.0
 */
public sealed interface TechnicalServiceRequestQueryFailure
        permits TechnicalServiceRequestQueryFailure.OrganizationNotFound,
        TechnicalServiceRequestQueryFailure.RequestNotFound {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements TechnicalServiceRequestQueryFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.organization-not-found";
        }
    }

    /** Technical service request not found failure. */
    record RequestNotFound() implements TechnicalServiceRequestQueryFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.request-not-found";
        }
    }
}
