package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities.MaintenanceSchedulePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for maintenance schedule persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface MaintenanceSchedulePersistenceRepository
        extends JpaRepository<MaintenanceSchedulePersistenceEntity, Long> {
    /**
     * Finds maintenance schedules by organization ordered by planned date.
     *
     * @param organizationId organization identifier
     * @return maintenance schedules
     */
    List<MaintenanceSchedulePersistenceEntity> findAllByOrganizationIdOrderByScheduledDateAsc(Long organizationId);

    /**
     * Finds one maintenance schedule by id and organization.
     *
     * @param id maintenance schedule identifier
     * @param organizationId organization identifier
     * @return maintenance schedule persistence entity when found
     */
    Optional<MaintenanceSchedulePersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);
}
