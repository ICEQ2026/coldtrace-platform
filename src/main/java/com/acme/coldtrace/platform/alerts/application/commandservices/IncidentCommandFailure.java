package com.acme.coldtrace.platform.alerts.application.commandservices;

/**
 * Failure types for incident command execution.
 *
 * @since 1.0
 */
public sealed interface IncidentCommandFailure
        permits IncidentCommandFailure.OrganizationNotFound,
        IncidentCommandFailure.IncidentNotFound,
        IncidentCommandFailure.AssetNotFound,
        IncidentCommandFailure.DeviceNotFound,
        IncidentCommandFailure.ReadingNotFound,
        IncidentCommandFailure.InconsistentReference,
        IncidentCommandFailure.AlreadyAcknowledged,
        IncidentCommandFailure.AlreadyEscalated,
        IncidentCommandFailure.AlreadyResolved,
        IncidentCommandFailure.InvalidLifecycleTransition {
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
    record OrganizationNotFound() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.organization-not-found";
        }
    }

    /** Incident not found failure. */
    record IncidentNotFound() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.incident-not-found";
        }
    }

    /**
     * Asset not found failure.
     * <p>
     * This failure protects organization-scoped incident creation from
     * referencing an asset that either does not exist or belongs to another
     * organization.
     */
    record AssetNotFound() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.asset-not-found";
        }
    }

    /** Referenced IoT device was not found in the organization. */
    record DeviceNotFound() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.device-not-found";
        }
    }

    /** Referenced sensor reading was not found in the organization. */
    record ReadingNotFound() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.reading-not-found";
        }
    }

    /** Incident references point to different operational contexts. */
    record InconsistentReference() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.inconsistent-reference";
        }
    }

    /** Incident has already been acknowledged. */
    record AlreadyAcknowledged() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.already-acknowledged";
        }
    }

    /** Incident has already been escalated. */
    record AlreadyEscalated() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.already-escalated";
        }
    }

    /** Incident has already been resolved. */
    record AlreadyResolved() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.already-resolved";
        }
    }

    /** Requested transition is not allowed. */
    record InvalidLifecycleTransition() implements IncidentCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.incident.error.invalid-lifecycle-transition";
        }
    }
}
