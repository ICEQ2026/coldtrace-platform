package com.acme.coldtrace.platform.maintenancemanagement.domain.repositories;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import java.util.List;
import java.util.Optional;

public interface TechnicalServiceRequestRepository {
    List<TechnicalServiceRequest> findAllByOrganizationId(Long organizationId);
    Optional<TechnicalServiceRequest> findByIdAndOrganizationId(Long id, Long organizationId);
    TechnicalServiceRequest save(TechnicalServiceRequest technicalServiceRequest);
}
