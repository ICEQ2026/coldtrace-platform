package com.acme.coldtrace.platform.maintenancemanagement.application.commandservices;

public sealed interface TechnicalServiceRequestCommandFailure permits
        TechnicalServiceRequestCommandFailure.OrganizationNotFound,
        TechnicalServiceRequestCommandFailure.AssetNotFound,
        TechnicalServiceRequestCommandFailure.IncidentNotFound,
        TechnicalServiceRequestCommandFailure.RequestNotFound,
        TechnicalServiceRequestCommandFailure.InvalidStatus,
        TechnicalServiceRequestCommandFailure.InvalidTransition,
        TechnicalServiceRequestCommandFailure.MissingClosureData {
    String messageKey();
    default Object[] args() { return new Object[0]; }
    record OrganizationNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.organization-not-found"; }
    }
    record AssetNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.asset-not-found"; }
    }
    record IncidentNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.incident-not-found"; }
    }
    record RequestNotFound() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.request-not-found"; }
    }
    record InvalidStatus() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.status.invalid"; }
    }
    record InvalidTransition() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.invalid-transition"; }
    }
    record MissingClosureData() implements TechnicalServiceRequestCommandFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.closure-data.required"; }
    }
}
