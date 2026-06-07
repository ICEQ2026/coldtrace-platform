package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateLocationCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.UpdateLocationResource;

/**
 * Interface layer translator converting update location resources to commands.
 *
 * @since 1.0
 */
public class UpdateLocationCommandFromResourceAssembler {
    /**
     * Converts an update location request into a command.
     *
     * @param resource update location request resource
     * @param organizationId organization identifier from the route
     * @param locationId location identifier from the route
     * @return update location command
     */
    public static UpdateLocationCommand toCommandFromResource(
            UpdateLocationResource resource,
            Long organizationId,
            Long locationId
    ) {
        return new UpdateLocationCommand(
                organizationId,
                locationId,
                resource.name(),
                resource.type(),
                resource.address(),
                resource.description(),
                resource.status()
        );
    }
}
