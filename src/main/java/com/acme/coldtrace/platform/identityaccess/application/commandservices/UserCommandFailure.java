package com.acme.coldtrace.platform.identityaccess.application.commandservices;

public sealed interface UserCommandFailure
        permits UserCommandFailure.DuplicateEmail,
        UserCommandFailure.OrganizationNotFound,
        UserCommandFailure.RoleNotFound {
    String messageKey();

    default Object[] args() {
        return new Object[0];
    }

    record DuplicateEmail() implements UserCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.duplicate-email";
        }
    }

    record OrganizationNotFound() implements UserCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.organization-not-found";
        }
    }

    record RoleNotFound() implements UserCommandFailure {
        @Override
        public String messageKey() {
            return "identity-access.user.error.role-not-found";
        }
    }
}
