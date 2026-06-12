package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.CreateAssetResource;

/**
 * Assembler that converts asset creation request resources into commands.
 * <p>
 * The organization identifier comes from the route, while the asset data comes
 * from the request body. Keeping this conversion in the interface layer avoids
 * leaking REST resource types into the application layer.
 *
 * @since 1.0
 */
public class CreateAssetCommandFromResourceAssembler {
    /**
     * Converts a create asset resource into a create command.
     *
     * @param resource request body resource
     * @param organizationId organization identifier from the route
     * @return create asset command
     */
    public static CreateAssetCommand toCommandFromResource(CreateAssetResource resource, Long organizationId) {
        return new CreateAssetCommand(
                organizationId,
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
