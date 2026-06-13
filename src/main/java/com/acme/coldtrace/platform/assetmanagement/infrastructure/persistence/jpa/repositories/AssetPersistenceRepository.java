package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.AssetUuid;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.AssetPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for asset persistence entities.
 * <p>
 * This repository is infrastructure-only. Application services use the domain
 * repository contract and remain unaware of JPA-specific entity classes.
 *
 * @since 1.0
 */
@Repository
public interface AssetPersistenceRepository extends JpaRepository<AssetPersistenceEntity, Long> {
    /**
     * Finds all asset persistence entities that belong to an organization.
     *
     * @param organizationId organization identifier
     * @return persistence entities for the organization
     */
    List<AssetPersistenceEntity> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one asset persistence entity by id and organization.
     *
     * @param id asset identifier
     * @param organizationId organization identifier
     * @return persistence entity when found
     */
    Optional<AssetPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether a business uuid is already present in an organization.
     *
     * @param organizationId organization identifier
     * @param uuid asset uuid value object
     * @return true when the uuid exists
     */
    boolean existsByOrganizationIdAndUuid(Long organizationId, AssetUuid uuid);

    /**
     * Checks whether another asset already uses the provided uuid.
     *
     * @param organizationId organization identifier
     * @param uuid asset uuid value object
     * @param id asset identifier excluded from the search
     * @return true when another entity uses the uuid
     */
    boolean existsByOrganizationIdAndUuidAndIdNot(Long organizationId, AssetUuid uuid, Long id);
}
