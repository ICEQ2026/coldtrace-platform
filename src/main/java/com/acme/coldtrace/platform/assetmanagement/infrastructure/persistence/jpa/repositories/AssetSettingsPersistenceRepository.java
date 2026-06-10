package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.AssetSettingsPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for asset settings persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface AssetSettingsPersistenceRepository extends JpaRepository<AssetSettingsPersistenceEntity, Long> {
    /**
     * Finds settings by organization.
     *
     * @param organizationId organization identifier
     * @return persistence entities for the organization
     */
    List<AssetSettingsPersistenceEntity> findAllByOrganizationId(Long organizationId);

    /**
     * Finds settings for one organization asset.
     *
     * @param organizationId organization identifier
     * @param assetId asset identifier
     * @return persistence entity when present
     */
    Optional<AssetSettingsPersistenceEntity> findByOrganizationIdAndAssetId(Long organizationId, Long assetId);

    /**
     * Finds organization default settings.
     *
     * @param organizationId organization identifier
     * @return default persistence entity when present
     */
    Optional<AssetSettingsPersistenceEntity> findFirstByOrganizationIdAndAssetIdIsNull(Long organizationId);
}
