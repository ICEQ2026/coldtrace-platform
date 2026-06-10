package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for querying and persisting {@link Incident} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    /**
     * Finds all incidents owned by an organization.
     *
     * @param organizationId organization identifier
     * @return incidents for the organization
     */
    List<Incident> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one incident scoped by organization.
     *
     * @param id incident identifier
     * @param organizationId organization identifier
     * @return incident when found
     */
    Optional<Incident> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether an incident exists in an organization.
     *
     * @param id incident identifier
     * @param organizationId organization identifier
     * @return true when the incident exists for the organization
     */
    boolean existsByIdAndOrganizationId(Long id, Long organizationId);
}
