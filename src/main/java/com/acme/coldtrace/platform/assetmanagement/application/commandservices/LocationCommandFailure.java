package com.acme.coldtrace.platform.assetmanagement.application.commandservices;

/**
 * Failure types for location command execution.
 *
 * @since 1.0
 */
public sealed interface LocationCommandFailure
        permits LocationCommandFailure.DuplicateName,
        LocationCommandFailure.OrganizationNotFound,
        LocationCommandFailure.LocationNotFound {
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

    /**
     * Duplicate location name failure.
     */
    record DuplicateName() implements LocationCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.location.error.name.duplicate";
        }
    }

    /**
     * Organization not found failure.
     */
    record OrganizationNotFound() implements LocationCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.location.error.organization-not-found";
        }
    }

    /**
     * Location not found failure.
     */
    record LocationNotFound() implements LocationCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.location.error.location-not-found";
        }
    }
}
