package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

public record PermissionResource(
        Long id,
        String resource,
        String action,
        String description
) {
}
