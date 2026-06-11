package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Request resource used to create a location.
 *
 * @param name location name
 * @param type location type
 * @param address optional location address
 * @param description optional location description
 * @param status location status
 * @since 1.0
 */
public record CreateLocationResource(
        String name,
        String type,
        String address,
        String description,
        String status
) {
}
