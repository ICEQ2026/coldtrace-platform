package com.acme.coldtrace.platform.maintenancemanagement.domain.repositories;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for corrective technical service request aggregates.
 *
 * @since 1.0
 */
public interface TechnicalServiceRequestRepository {
    /**
     * Finds technical service requests owned by an organization.
     *
     * @param organizationId organization identifier
     * @return organization technical service requests
     */
    List<TechnicalServiceRequest> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one technical service request by id and organization.
     *
     * @param id technical service request identifier
     * @param organizationId organization identifier
     * @return technical service request when found
     */
    Optional<TechnicalServiceRequest> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Persists a technical service request aggregate.
     *
     * @param technicalServiceRequest request aggregate to persist
     * @return persisted request rebuilt from persistence state
     */
    TechnicalServiceRequest save(TechnicalServiceRequest technicalServiceRequest);
}
