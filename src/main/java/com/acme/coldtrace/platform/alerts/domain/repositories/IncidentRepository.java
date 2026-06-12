package com.acme.coldtrace.platform.alerts.domain.repositories;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for incident aggregates.
 *
 * @since 1.0
 */
public interface IncidentRepository {
    List<Incident> findAllByOrganizationId(Long organizationId);

    Optional<Incident> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByIdAndOrganizationId(Long id, Long organizationId);

    Incident save(Incident incident);
}
