package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting user query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromUserQueryResultAssembler {
    /**
     * Converts a list of users into a 200 HTTP response.
     *
     * @param users user query result
     * @return response entity containing user resources
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<User> users) {
        return ResponseEntity.ok(users.stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
