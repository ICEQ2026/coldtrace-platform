package com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for sensor reading persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface SensorReadingPersistenceRepository extends JpaRepository<SensorReadingPersistenceEntity, Long> {
    /**
     * Finds readings by organization ordered by most recent first.
     *
     * @param organizationId organization identifier
     * @return organization readings
     */
    List<SensorReadingPersistenceEntity> findAllByOrganizationIdOrderByRecordedAtDesc(Long organizationId);

    /**
     * Finds one reading by id and organization.
     *
     * @param id reading identifier
     * @param organizationId organization identifier
     * @return persistence entity when found
     */
    Optional<SensorReadingPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);
}
