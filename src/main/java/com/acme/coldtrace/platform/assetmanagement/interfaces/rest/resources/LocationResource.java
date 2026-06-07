package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

/**
 * Response resource representing an operational location.
 *
 * @param id location identifier
 * @param organizationId organization identifier
 * @param name location name
 * @param type location type
 * @param address optional location address
 * @param description optional location description
 * @param status location status
 * @since 1.0
 */
public record LocationResource(
        Long id,
        Long organizationId,
        String name,
        String type,
        String address,
        String description,
        String status
) {
}
