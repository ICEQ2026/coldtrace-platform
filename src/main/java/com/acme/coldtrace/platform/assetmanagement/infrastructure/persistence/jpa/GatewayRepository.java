package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for querying and persisting {@link Gateway} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long> {
    /**
     * Finds all gateways that belong to an organization.
     *
     * @param organizationId organization identifier
     * @return gateways for the organization
     */
    List<Gateway> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one gateway by id and organization.
     *
     * @param id gateway identifier
     * @param organizationId organization identifier
     * @return gateway when found
     */
    Optional<Gateway> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether a gateway uuid already exists in the organization.
     *
     * @param organizationId organization identifier
     * @param uuid gateway unique identifier
     * @return true when a gateway with the provided uuid exists
     */
    boolean existsByOrganizationIdAndUuidIgnoreCase(Long organizationId, String uuid);

    /**
     * Checks whether a gateway uuid is used by another gateway in the organization.
     *
     * @param organizationId organization identifier
     * @param uuid gateway unique identifier
     * @param id gateway identifier to exclude
     * @return true when another gateway with the provided uuid exists
     */
    boolean existsByOrganizationIdAndUuidIgnoreCaseAndIdNot(Long organizationId, String uuid, Long id);
}
