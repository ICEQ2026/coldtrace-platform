package com.acme.coldtrace.platform.identityaccess.application.commandservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;

/**
 * Application result for the organization sign-up use case.
 *
 * @param organization organization created by the sign-up flow
 * @param user first user created for the organization
 * @since 1.0
 */
public record OrganizationSignUpCommandResult(
        Organization organization,
        User user
) {
}
