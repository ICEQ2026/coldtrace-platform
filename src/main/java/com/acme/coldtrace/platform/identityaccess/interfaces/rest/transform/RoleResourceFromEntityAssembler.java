package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects.Permission;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.PermissionResource;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role entity) {
        return new RoleResource(
                entity.getId(),
                entity.getName(),
                entity.getLabel(),
                entity.getPermissions().stream()
                        .map(RoleResourceFromEntityAssembler::toPermissionResourceFromValueObject)
                        .toList()
        );
    }

    private static PermissionResource toPermissionResourceFromValueObject(Permission permission) {
        return new PermissionResource(
                permission.getId(),
                permission.getResource(),
                permission.getAction(),
                permission.getDescription()
        );
    }
}
