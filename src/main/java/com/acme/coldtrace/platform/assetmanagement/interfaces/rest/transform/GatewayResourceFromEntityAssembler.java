package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.GatewayResource;

/**
 * Interface layer translator converting gateway aggregates to resources.
 *
 * @since 1.0
 */
public class GatewayResourceFromEntityAssembler {
    /**
     * Converts a gateway aggregate to a GatewayResource.
     *
     * @param entity gateway aggregate
     * @return gateway response resource
     */
    public static GatewayResource toResourceFromEntity(Gateway entity) {
        return new GatewayResource(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getLocationId(),
                entity.getUuid(),
                entity.getName(),
                entity.getNetwork(),
                entity.getStatus()
        );
    }
}
