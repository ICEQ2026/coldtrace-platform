package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Request resource for updating an asset.
 *
 * @param locationId location identifier where the asset is placed
 * @param uuid asset unique identifier inside the organization
 * @param type business asset type
 * @param name asset display name
 * @param capacity asset capacity
 * @param description optional asset description
 * @param status asset operational status
 * @since 1.0
 */
public record UpdateAssetResource(
        Long locationId,
        String uuid,
        String type,
        String name,
        Double capacity,
        String description,
        String status
) {
}
