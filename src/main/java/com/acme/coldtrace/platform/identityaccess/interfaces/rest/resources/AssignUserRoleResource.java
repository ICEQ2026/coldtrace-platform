package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

/**
 * Request resource used to assign a role to an organization user.
 *
 * @param roleId target role identifier
 * @since 1.0
 */
public record AssignUserRoleResource(
        Long roleId
) {
}
