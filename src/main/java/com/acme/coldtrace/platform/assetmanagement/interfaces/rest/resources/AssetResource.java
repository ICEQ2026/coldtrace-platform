package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Response resource representing an asset.
 *
 * @param id asset identifier
 * @param organizationId organization identifier that owns the asset
 * @param locationId location identifier where the asset is placed
 * @param uuid asset unique identifier inside the organization
 * @param type business asset type
 * @param name asset display name
 * @param capacity asset capacity
 * @param description optional asset description
 * @param status asset operational status
 * @since 1.0
 */
public record AssetResource(
        Long id,
        Long organizationId,
        Long locationId,
        String uuid,
        String type,
        String name,
        Double capacity,
        String description,
        String status
) {
}
