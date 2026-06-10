package com.acme.coldtrace.platform.assetmanagement.domain.repositories;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for asset settings aggregates.
 *
 * @since 1.0
 */
public interface AssetSettingsRepository {
    /**
     * Finds all settings registered for an organization.
     *
     * @param organizationId organization identifier
     * @return organization settings
     */
    List<AssetSettings> findAllByOrganizationId(Long organizationId);

    /**
     * Finds settings for one asset inside an organization.
     *
     * @param organizationId organization identifier
     * @param assetId asset identifier
     * @return settings when present
     */
    Optional<AssetSettings> findByOrganizationIdAndAssetId(Long organizationId, Long assetId);

    /**
     * Finds the default settings for an organization.
     *
     * @param organizationId organization identifier
     * @return default settings when present
     */
    Optional<AssetSettings> findDefaultByOrganizationId(Long organizationId);

    /**
     * Persists asset settings.
     *
     * @param assetSettings settings aggregate
     * @return persisted settings rebuilt from persistence state
     */
    AssetSettings save(AssetSettings assetSettings);
}
