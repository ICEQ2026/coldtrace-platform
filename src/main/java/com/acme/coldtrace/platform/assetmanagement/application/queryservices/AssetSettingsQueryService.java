package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetSettingsByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetEffectiveAssetSettingsByAssetIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for asset settings query operations.
 *
 * @since 1.0
 */
public interface AssetSettingsQueryService {
    /**
     * Retrieves all settings that belong to an organization.
     *
     * @param query query object containing the organization identifier
     * @return organization settings, possibly empty
     */
    List<AssetSettings> handle(GetAssetSettingsByOrganizationIdQuery query);

    /**
     * Retrieves effective settings for an asset.
     *
     * @param query query object containing organization and asset identifiers
     * @return asset-specific settings or organization default settings when present
     */
    Optional<AssetSettings> handle(GetEffectiveAssetSettingsByAssetIdQuery query);
}
