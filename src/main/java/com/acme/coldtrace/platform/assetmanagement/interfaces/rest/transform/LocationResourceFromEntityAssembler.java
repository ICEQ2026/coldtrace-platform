package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Location;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.LocationResource;

/**
 * Interface layer translator converting location aggregates to resources.
 *
 * @since 1.0
 */
public class LocationResourceFromEntityAssembler {
    /**
     * Converts a location aggregate to a LocationResource.
     *
     * @param entity location aggregate
     * @return location response resource
     */
    public static LocationResource toResourceFromEntity(Location entity) {
        return new LocationResource(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getType(),
                entity.getAddress(),
                entity.getDescription(),
                entity.getStatus()
        );
    }
}
