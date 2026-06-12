package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

/**
 * Request resource used to create a user.
 *
 * @param firstName user first name
 * @param lastName user last name
 * @param email user email address
 * @param roleId role identifier
 * @since 1.0
 */
public record CreateUserResource(
        String firstName,
        String lastName,
        String email,
        Long roleId
) {
}
