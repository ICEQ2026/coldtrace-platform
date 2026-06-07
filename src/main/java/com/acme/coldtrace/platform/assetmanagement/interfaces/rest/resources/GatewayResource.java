package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Response resource representing a gateway.
 *
 * @param id gateway identifier
 * @param organizationId organization identifier
 * @param locationId location identifier
 * @param uuid gateway unique identifier
 * @param name gateway name
 * @param network gateway network name
 * @param status gateway status
 * @since 1.0
 */
public record GatewayResource(
        Long id,
        Long organizationId,
        Long locationId,
        String uuid,
        String name,
        String network,
        String status
) {
}
