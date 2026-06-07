package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Request resource used to create a gateway.
 *
 * @param locationId location identifier
 * @param uuid gateway unique identifier
 * @param name gateway name
 * @param network gateway network name
 * @param status gateway status
 * @since 1.0
 */
public record CreateGatewayResource(
        Long locationId,

        String uuid,

        String name,

        String network,

        String status
) {
}
