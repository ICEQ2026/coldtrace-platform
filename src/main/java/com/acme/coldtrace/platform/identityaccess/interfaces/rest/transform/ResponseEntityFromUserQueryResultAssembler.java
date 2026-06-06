package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromUserQueryResultAssembler {
    public static ResponseEntity<?> toResponseEntityFromList(List<User> users) {
        return ResponseEntity.ok(users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
