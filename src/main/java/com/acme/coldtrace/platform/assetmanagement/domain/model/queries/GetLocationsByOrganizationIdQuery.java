package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving locations by organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetLocationsByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     *
     * @throws IllegalArgumentException if the organization identifier is invalid
     */
    public GetLocationsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.location.error.organizationId.invalid");
        }
    }
}
