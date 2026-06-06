package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

public record UserResource(
        Long id,
        String uuid,
        Long organizationUserId,
        String firstName,
        String lastName,
        String email,
        Long organizationId,
        Long roleId
) {
}
