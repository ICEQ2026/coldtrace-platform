package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting role query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromRoleQueryResultAssembler {
    /**
     * Converts a list of roles into a 200 HTTP response.
     *
     * @param roles role query result
     * @return response entity containing role resources
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<Role> roles) {
        return ResponseEntity.ok(roles.stream()
                .map(RoleResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
