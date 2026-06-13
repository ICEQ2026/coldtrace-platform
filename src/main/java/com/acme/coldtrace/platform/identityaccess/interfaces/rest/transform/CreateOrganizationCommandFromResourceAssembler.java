package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.CreateOrganizationResource;

/**
 * Interface layer translator converting organization creation resources to commands.
 *
 * @since 1.0
 */
public class CreateOrganizationCommandFromResourceAssembler {
    /**
     * Converts a CreateOrganizationResource to a CreateOrganizationCommand.
     *
     * @param resource organization creation request resource
     * @return create organization command
     */
    public static CreateOrganizationCommand toCommandFromResource(CreateOrganizationResource resource) {
        return new CreateOrganizationCommand(
                resource.legalName(),
                resource.commercialName(),
                resource.taxId(),
                resource.contactEmail()
        );
    }
}
