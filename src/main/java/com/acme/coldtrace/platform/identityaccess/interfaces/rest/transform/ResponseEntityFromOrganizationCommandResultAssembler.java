package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting organization command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromOrganizationCommandResultAssembler {
    /**
     * Converts a created organization into a 201 HTTP response.
     *
     * @param organization created organization aggregate
     * @return response entity containing the organization resource
     */
    public static ResponseEntity<?> toResponseEntityFromEntity(Organization organization) {
        return new ResponseEntity<>(
                OrganizationResourceFromEntityAssembler.toResourceFromEntity(organization),
                CREATED
        );
    }
}
