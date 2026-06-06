package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.OrganizationResource;

public class OrganizationResourceFromEntityAssembler {
    public static OrganizationResource toResourceFromEntity(Organization entity) {
        return new OrganizationResource(
                entity.getId(),
                entity.getLegalName(),
                entity.getCommercialName(),
                entity.getTaxId(),
                entity.getContactEmail()
        );
    }
}
