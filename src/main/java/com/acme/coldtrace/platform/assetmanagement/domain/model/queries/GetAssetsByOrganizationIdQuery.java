package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving assets that belong to an organization.
 *
 * @param organizationId organization identifier used to scope the search
 * @since 1.0
 */
public record GetAssetsByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier before the query reaches the
     * persistence layer.
     *
     * @throws IllegalArgumentException if the organization identifier is null or
     *                                  not positive
     */
    public GetAssetsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.asset.error.organizationId.invalid");
        }
    }
}
