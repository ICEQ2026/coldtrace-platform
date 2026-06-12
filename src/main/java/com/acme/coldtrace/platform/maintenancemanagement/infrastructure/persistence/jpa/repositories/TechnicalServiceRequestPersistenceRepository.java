package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities.TechnicalServiceRequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for technical service request persistence entities.
 *
 * @since 1.0
 */
public interface TechnicalServiceRequestPersistenceRepository
        extends JpaRepository<TechnicalServiceRequestPersistenceEntity, Long> {
    /**
     * Finds requests by organization ordered by most recent first.
     *
     * @param organizationId organization identifier
     * @return technical service requests
     */
    List<TechnicalServiceRequestPersistenceEntity> findAllByOrganizationIdOrderByRequestedAtDesc(Long organizationId);

    /**
     * Finds one request by id and organization.
     *
     * @param id technical service request identifier
     * @param organizationId organization identifier
     * @return technical service request persistence entity when found
     */
    Optional<TechnicalServiceRequestPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);
}
