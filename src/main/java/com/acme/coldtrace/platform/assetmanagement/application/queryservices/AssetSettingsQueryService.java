package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetSettingsByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetEffectiveAssetSettingsByAssetIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for asset settings query operations.
 * <p>
 * The interface layer uses this service to retrieve organization-level and
 * asset-effective settings without depending on persistence details. Query
 * methods keep the organization boundary explicit because asset settings are
 * operational rules owned by a single ColdTrace organization.
 *
 * @since 1.0
 */
public interface AssetSettingsQueryService {
    /**
     * Retrieves all asset settings that belong to an organization.
     * <p>
     * The returned collection can include one organization default configuration and
     * any number of asset-specific configurations. An empty collection means the
     * organization has no settings registered yet.
     *
     * @param query query object containing the organization identifier
     * @return list of settings owned by the organization, possibly empty
     * @see GetAssetSettingsByOrganizationIdQuery
     */
    List<AssetSettings> handle(GetAssetSettingsByOrganizationIdQuery query);

    /**
     * Retrieves effective settings for an asset.
     * <p>
     * Effective settings are resolved by first searching for a configuration tied to
     * the asset and then falling back to the organization's default configuration.
     * The result is explicit so callers can distinguish an invalid asset from an
     * asset that exists but still has no effective thresholds.
     *
     * @param query query object containing organization and asset identifiers
     * @return success with asset-specific settings or organization default settings, otherwise a query failure
     * @see GetEffectiveAssetSettingsByAssetIdQuery
     */
    Result<AssetSettings, AssetSettingsQueryFailure> handle(GetEffectiveAssetSettingsByAssetIdQuery query);
}
