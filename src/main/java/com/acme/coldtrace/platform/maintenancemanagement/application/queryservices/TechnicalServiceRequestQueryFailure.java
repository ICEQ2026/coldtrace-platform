package com.acme.coldtrace.platform.maintenancemanagement.application.queryservices;

public sealed interface TechnicalServiceRequestQueryFailure permits
        TechnicalServiceRequestQueryFailure.OrganizationNotFound,
        TechnicalServiceRequestQueryFailure.RequestNotFound {
    String messageKey();
    default Object[] args() { return new Object[0]; }
    record OrganizationNotFound() implements TechnicalServiceRequestQueryFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.organization-not-found"; }
    }
    record RequestNotFound() implements TechnicalServiceRequestQueryFailure {
        public String messageKey() { return "maintenance-management.technical-service-request.error.request-not-found"; }
    }
}
