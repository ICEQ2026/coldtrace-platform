package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving the effective settings for one asset.
 * <p>
 * Effective settings mean asset-specific settings when they exist, otherwise
 * the organization default settings.
 *
 * @param organizationId organization identifier
 * @param assetId asset identifier
 * @since 1.0
 */
public record GetEffectiveAssetSettingsByAssetIdQuery(Long organizationId, Long assetId) {
    /**
     * Validates route identifiers.
     *
     * @throws IllegalArgumentException if any identifier is invalid
     */
    public GetEffectiveAssetSettingsByAssetIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.asset-settings.error.organizationId.invalid");
        }
        if (assetId == null || assetId <= 0) {
            throw new IllegalArgumentException("asset-management.asset-settings.error.assetId.invalid");
        }
    }
}
