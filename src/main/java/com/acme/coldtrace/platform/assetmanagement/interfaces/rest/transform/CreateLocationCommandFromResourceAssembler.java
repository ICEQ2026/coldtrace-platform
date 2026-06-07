package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateLocationCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.CreateLocationResource;

/**
 * Interface layer translator converting create location resources to commands.
 *
 * @since 1.0
 */
public class CreateLocationCommandFromResourceAssembler {
    /**
     * Converts a create location request into a command.
     *
     * @param resource create location request resource
     * @param organizationId organization identifier from the route
     * @return create location command
     */
    public static CreateLocationCommand toCommandFromResource(
            CreateLocationResource resource,
            Long organizationId
    ) {
        return new CreateLocationCommand(
                organizationId,
                resource.name(),
                resource.type(),
                resource.address(),
                resource.description(),
                resource.status()
        );
    }
}
