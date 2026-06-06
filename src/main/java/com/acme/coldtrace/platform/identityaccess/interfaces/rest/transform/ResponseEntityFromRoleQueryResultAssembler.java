package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromRoleQueryResultAssembler {
    public static ResponseEntity<?> toResponseEntityFromList(List<Role> roles) {
        return ResponseEntity.ok(roles.stream()
                .map(RoleResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
