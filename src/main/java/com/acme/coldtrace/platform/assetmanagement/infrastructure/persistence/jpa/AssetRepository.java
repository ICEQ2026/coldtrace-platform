package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for querying and persisting {@link Asset} aggregates.
 * <p>
 * All custom query methods include the organization identifier because assets
 * are organization-scoped resources. This keeps application services from
 * accidentally loading an asset by global id and then applying mutations outside
 * the selected tenant boundary.
 *
 * @since 1.0
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    /**
     * Finds all assets that belong to an organization.
     *
     * @param organizationId organization identifier
     * @return assets for the organization
     */
    List<Asset> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one asset by id and organization.
     *
     * @param id asset identifier
     * @param organizationId organization identifier
     * @return asset when found
     */
    Optional<Asset> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether an asset uuid already exists in the organization.
     *
     * @param organizationId organization identifier
     * @param uuid asset unique identifier
     * @return true when an asset with the provided uuid exists
     */
    boolean existsByOrganizationIdAndUuidIgnoreCase(Long organizationId, String uuid);

    /**
     * Checks whether an asset uuid is used by another asset in the organization.
     *
     * @param organizationId organization identifier
     * @param uuid asset unique identifier
     * @param id asset identifier to exclude
     * @return true when another asset with the provided uuid exists
     */
    boolean existsByOrganizationIdAndUuidIgnoreCaseAndIdNot(Long organizationId, String uuid, Long id);
}
