package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving one location by id and organization.
 *
 * @param organizationId organization identifier
 * @param locationId location identifier
 * @since 1.0
 */
public record GetLocationByIdAndOrganizationIdQuery(Long organizationId, Long locationId) {
    /**
     * Validates location query identifiers.
     *
     * @throws IllegalArgumentException if identifiers are invalid
     */
    public GetLocationByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.location.error.organizationId.invalid");
        }
        if (locationId == null || locationId <= 0) {
            throw new IllegalArgumentException("asset-management.location.error.locationId.invalid");
        }
    }
}
