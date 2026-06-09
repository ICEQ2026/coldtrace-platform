package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.AssetResource;

/**
 * Assembler that converts asset aggregates into REST response resources.
 *
 * @since 1.0
 */
public class AssetResourceFromEntityAssembler {
    /**
     * Converts an asset aggregate into an asset resource.
     *
     * @param asset asset aggregate to expose through REST
     * @return asset response resource
     */
    public static AssetResource toResourceFromEntity(Asset asset) {
        return new AssetResource(
                asset.getId(),
                asset.getOrganizationId(),
                asset.getLocationId(),
                asset.getUuid(),
                asset.getType(),
                asset.getName(),
                asset.getCapacity(),
                asset.getDescription(),
                asset.getStatus()
        );
    }
}
