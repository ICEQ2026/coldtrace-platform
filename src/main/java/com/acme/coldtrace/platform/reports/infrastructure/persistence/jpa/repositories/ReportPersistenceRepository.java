package com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.entities.ReportPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for report persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface ReportPersistenceRepository extends JpaRepository<ReportPersistenceEntity, Long> {
    /**
     * Finds reports by organization ordered by most recent first.
     *
     * @param organizationId organization identifier
     * @return generated reports
     */
    List<ReportPersistenceEntity> findAllByOrganizationIdOrderByGeneratedAtDesc(Long organizationId);

    /**
     * Finds one report by id and organization.
     *
     * @param id report identifier
     * @param organizationId organization identifier
     * @return report persistence entity when found
     */
    Optional<ReportPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);
}
