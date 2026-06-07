package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateGatewayCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.UpdateGatewayResource;

/**
 * Interface layer translator converting update gateway resources to commands.
 *
 * @since 1.0
 */
public class UpdateGatewayCommandFromResourceAssembler {
    /**
     * Converts an update gateway request into a command.
     *
     * @param resource update gateway request resource
     * @param organizationId organization identifier from the route
     * @param gatewayId gateway identifier from the route
     * @return update gateway command
     */
    public static UpdateGatewayCommand toCommandFromResource(
            UpdateGatewayResource resource,
            Long organizationId,
            Long gatewayId
    ) {
        return new UpdateGatewayCommand(
                organizationId,
                gatewayId,
                resource.locationId(),
                resource.uuid(),
                resource.name(),
                resource.network(),
                resource.status()
        );
    }
}
