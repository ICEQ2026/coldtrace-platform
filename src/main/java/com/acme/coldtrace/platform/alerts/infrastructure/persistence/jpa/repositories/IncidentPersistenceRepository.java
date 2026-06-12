package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for incident persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface IncidentPersistenceRepository extends JpaRepository<IncidentPersistenceEntity, Long> {
    List<IncidentPersistenceEntity> findAllByOrganizationId(Long organizationId);

    Optional<IncidentPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByIdAndOrganizationId(Long id, Long organizationId);
}
