package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateGatewayCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.CreateGatewayResource;

/**
 * Interface layer translator converting create gateway resources to commands.
 *
 * @since 1.0
 */
public class CreateGatewayCommandFromResourceAssembler {
    /**
     * Converts a create gateway request into a command.
     *
     * @param resource create gateway request resource
     * @param organizationId organization identifier from the route
     * @return create gateway command
     */
    public static CreateGatewayCommand toCommandFromResource(
            CreateGatewayResource resource,
            Long organizationId
    ) {
        return new CreateGatewayCommand(
                organizationId,
                resource.locationId(),
                resource.uuid(),
                resource.name(),
                resource.network(),
                resource.status()
        );
    }
}
