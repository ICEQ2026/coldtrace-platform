package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromOrganizationQueryResultAssembler {
    public static ResponseEntity<?> toResponseEntityFromList(List<Organization> organizations) {
        return ResponseEntity.ok(organizations.stream()
                .map(OrganizationResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
