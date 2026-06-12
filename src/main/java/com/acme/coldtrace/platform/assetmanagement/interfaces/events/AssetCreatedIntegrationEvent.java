package com.acme.coldtrace.platform.assetmanagement.interfaces.events;

/**
 * Integration event published by asset management when an asset is created.
 *
 * @param assetId asset identifier
 * @param organizationId owning organization identifier
 * @param locationId location identifier
 * @param uuid business asset UUID
 * @param name asset display name
 * @since 1.0
 */
public record AssetCreatedIntegrationEvent(
        Long assetId,
        Long organizationId,
        Long locationId,
        String uuid,
        String name
) {
}
