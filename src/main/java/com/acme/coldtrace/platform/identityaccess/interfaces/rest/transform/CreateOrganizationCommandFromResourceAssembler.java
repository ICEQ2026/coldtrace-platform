package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.CreateOrganizationResource;

public class CreateOrganizationCommandFromResourceAssembler {
    public static CreateOrganizationCommand toCommandFromResource(CreateOrganizationResource resource) {
        return new CreateOrganizationCommand(
                resource.legalName(),
                resource.commercialName(),
                resource.taxId(),
                resource.contactEmail()
        );
    }
}
