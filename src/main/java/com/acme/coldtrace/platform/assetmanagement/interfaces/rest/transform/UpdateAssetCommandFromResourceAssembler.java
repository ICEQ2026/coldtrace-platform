package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.UpdateAssetResource;

/**
 * Assembler that converts asset update request resources into commands.
 * <p>
 * Route identifiers are intentionally passed separately from the request body so
 * clients cannot move an update outside the selected organization or target a
 * different asset through payload data.
 *
 * @since 1.0
 */
public class UpdateAssetCommandFromResourceAssembler {
    /**
     * Converts an update asset resource into an update command.
     *
     * @param resource request body resource
     * @param organizationId organization identifier from the route
     * @param assetId asset identifier from the route
     * @return update asset command
     */
    public static UpdateAssetCommand toCommandFromResource(
            UpdateAssetResource resource,
            Long organizationId,
            Long assetId
    ) {
        return new UpdateAssetCommand(
                organizationId,
                assetId,
                resource.locationId(),
                resource.uuid(),
                resource.type(),
                resource.name(),
                resource.capacity(),
                resource.description(),
                resource.status()
        );
    }
}
