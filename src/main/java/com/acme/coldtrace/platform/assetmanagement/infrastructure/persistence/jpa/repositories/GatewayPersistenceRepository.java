package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.GatewayUuid;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.GatewayPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for gateway persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface GatewayPersistenceRepository extends JpaRepository<GatewayPersistenceEntity, Long> {
    /**
     * Finds gateways by organization.
     *
     * @param organizationId organization identifier
     * @return persistence entities for the organization
     */
    List<GatewayPersistenceEntity> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one gateway by id and organization.
     *
     * @param id gateway identifier
     * @param organizationId organization identifier
     * @return persistence entity when found
     */
    Optional<GatewayPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether a gateway uuid already exists in an organization.
     *
     * @param organizationId organization identifier
     * @param uuid gateway uuid value object
     * @return true when the uuid exists
     */
    boolean existsByOrganizationIdAndUuid(Long organizationId, GatewayUuid uuid);

    /**
     * Checks whether another gateway uses a gateway uuid.
     *
     * @param organizationId organization identifier
     * @param uuid gateway uuid value object
     * @param id gateway identifier excluded from the search
     * @return true when another entity uses the uuid
     */
    boolean existsByOrganizationIdAndUuidAndIdNot(Long organizationId, GatewayUuid uuid, Long id);
}
