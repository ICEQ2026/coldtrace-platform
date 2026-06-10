package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving all asset settings owned by an organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetAssetSettingsByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     *
     * @throws IllegalArgumentException if the organization identifier is invalid
     */
    public GetAssetSettingsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.asset-settings.error.organizationId.invalid");
        }
    }
}
