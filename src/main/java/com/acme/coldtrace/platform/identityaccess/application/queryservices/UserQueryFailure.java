package com.acme.coldtrace.platform.identityaccess.application.queryservices;

/**
 * Failure types for user query execution.
 *
 * @since 1.0
 */
public sealed interface UserQueryFailure
        permits UserQueryFailure.OrganizationNotFound {
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
     * Organization not found failure.
     */
    record OrganizationNotFound() implements UserQueryFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.organization-not-found";
        }
    }
}
