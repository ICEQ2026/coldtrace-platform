package com.acme.coldtrace.platform.maintenancemanagement.application.commandservices;

/**
 * Failure types produced while executing technical service request commands.
 *
 * @since 1.0
 */
public sealed interface TechnicalServiceRequestCommandFailure
        permits TechnicalServiceRequestCommandFailure.OrganizationNotFound,
        TechnicalServiceRequestCommandFailure.AssetNotFound,
        TechnicalServiceRequestCommandFailure.IncidentNotFound,
        TechnicalServiceRequestCommandFailure.InconsistentIncidentReference,
        TechnicalServiceRequestCommandFailure.RequestNotFound,
        TechnicalServiceRequestCommandFailure.InvalidStatus,
        TechnicalServiceRequestCommandFailure.InvalidTransition,
        TechnicalServiceRequestCommandFailure.MissingClosureData {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.organization-not-found";
        }
    }

    /** Asset not found or not owned by the organization failure. */
    record AssetNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.asset-not-found";
        }
    }

    /** Incident not found or not owned by the organization failure. */
    record IncidentNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.incident-not-found";
        }
    }

    /** Incident does not reference the requested asset failure. */
    record InconsistentIncidentReference() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.inconsistent-incident-reference";
        }
    }

    /** Technical service request not found failure. */
    record RequestNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.request-not-found";
        }
    }

    /** Invalid status value failure. */
    record InvalidStatus() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.status.invalid";
        }
    }

    /** Invalid lifecycle transition failure. */
    record InvalidTransition() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.invalid-transition";
        }
    }

    /** Missing closure data failure. */
    record MissingClosureData() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() {
            return "maintenance-management.technical-service-request.error.closure-data.required";
        }
    }
}
