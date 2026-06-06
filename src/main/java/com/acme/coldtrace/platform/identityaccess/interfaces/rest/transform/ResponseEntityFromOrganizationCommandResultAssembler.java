package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

public class ResponseEntityFromOrganizationCommandResultAssembler {
    public static ResponseEntity<?> toResponseEntityFromEntity(Organization organization) {
        return new ResponseEntity<>(
                OrganizationResourceFromEntityAssembler.toResourceFromEntity(organization),
                CREATED
        );
    }
}
