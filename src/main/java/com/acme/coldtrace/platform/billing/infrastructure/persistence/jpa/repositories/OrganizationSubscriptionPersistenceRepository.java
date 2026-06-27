package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities.OrganizationSubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for organization subscription persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface OrganizationSubscriptionPersistenceRepository
        extends JpaRepository<OrganizationSubscriptionPersistenceEntity, Long> {
    /**
     * Finds the current subscription for an organization.
     *
     * @param organizationId organization identifier
     * @return persistence entity when found
     */
    Optional<OrganizationSubscriptionPersistenceEntity> findByOrganizationId(Long organizationId);
}
