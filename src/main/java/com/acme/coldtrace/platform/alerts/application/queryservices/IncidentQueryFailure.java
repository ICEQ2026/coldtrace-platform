package com.acme.coldtrace.platform.alerts.application.queryservices;

/**
 * Failure types for incident query execution.
 *
 * @since 1.0
 */
public sealed interface IncidentQueryFailure
        permits IncidentQueryFailure.OrganizationNotFound, IncidentQueryFailure.IncidentNotFound {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements IncidentQueryFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.organization-not-found";
        }
    }

    /** Incident not found failure. */
    record IncidentNotFound() implements IncidentQueryFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.incident-not-found";
        }
    }
}
