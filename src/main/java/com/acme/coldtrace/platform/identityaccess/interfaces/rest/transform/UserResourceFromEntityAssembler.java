package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
                entity.getId(),
                entity.getUuid() == null ? "USR-" + entity.getId() : entity.getUuid(),
                entity.getOrganizationUserId() == null ? entity.getId() : entity.getOrganizationUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getOrganizationId(),
                entity.getRoleId()
        );
    }
}
