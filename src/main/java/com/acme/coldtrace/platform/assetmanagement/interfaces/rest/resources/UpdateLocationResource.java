package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Request resource used to update a location.
 *
 * @param name location name
 * @param type location type
 * @param address optional location address
 * @param description optional location description
 * @param status location status
 * @since 1.0
 */
public record UpdateLocationResource(
        String name,

        String type,

        String address,

        String description,

        String status
) {
}
