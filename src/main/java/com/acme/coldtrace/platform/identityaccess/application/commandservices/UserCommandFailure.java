package com.acme.coldtrace.platform.identityaccess.application.commandservices;

/**
 * Failure types for user command execution.
 *
 * @since 1.0
 */
public sealed interface UserCommandFailure
        permits UserCommandFailure.DuplicateEmail,
        UserCommandFailure.OrganizationNotFound,
        UserCommandFailure.RoleNotFound {
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
     * Duplicate email failure.
     */
    record DuplicateEmail() implements UserCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.duplicate-email";
        }
    }

    /**
     * Organization not found failure.
     */
    record OrganizationNotFound() implements UserCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.organization-not-found";
        }
    }

    /**
     * Role not found failure.
     */
    record RoleNotFound() implements UserCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.role-not-found";
        }
    }
}
