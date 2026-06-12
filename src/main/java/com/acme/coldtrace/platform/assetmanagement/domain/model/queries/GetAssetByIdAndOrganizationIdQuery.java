package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving a single asset by id within an organization boundary.
 *
 * @param organizationId organization identifier used to scope the lookup
 * @param assetId asset identifier to retrieve
 * @since 1.0
 */
public record GetAssetByIdAndOrganizationIdQuery(Long organizationId, Long assetId) {
    /**
     * Validates the identifiers before the query reaches the persistence layer.
     *
     * @throws IllegalArgumentException if any identifier is null or not positive
     */
    public GetAssetByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.asset.error.organizationId.invalid");
        }
        if (assetId == null || assetId <= 0) {
            throw new IllegalArgumentException("asset-management.asset.error.assetId.invalid");
        }
    }
}
