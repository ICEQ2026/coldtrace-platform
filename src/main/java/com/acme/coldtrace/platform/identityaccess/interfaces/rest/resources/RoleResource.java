package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import java.util.List;

public record RoleResource(
        Long id,
        String name,
        String label,
        List<PermissionResource> permissions
) {
}
