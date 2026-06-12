package com.acme.coldtrace.platform.assetmanagement.domain.model.events;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;

/**
 * Domain event raised when an asset is created.
 *
 * @param assetId asset identifier
 * @param organizationId owning organization identifier
 * @param locationId location identifier where the asset is placed
 * @param uuid business asset UUID
 * @param name asset display name
 * @since 1.0
 */
public record AssetCreatedEvent(
        Long assetId,
        Long organizationId,
        Long locationId,
        String uuid,
        String name
) {
    /**
     * Builds the event from an asset aggregate.
     *
     * @param asset source aggregate
     * @return asset-created event
     */
    public static AssetCreatedEvent from(Asset asset) {
        return new AssetCreatedEvent(
                asset.getId(),
                asset.getOrganizationId(),
                asset.getLocationId(),
                asset.getUuid(),
                asset.getName()
        );
    }
}
