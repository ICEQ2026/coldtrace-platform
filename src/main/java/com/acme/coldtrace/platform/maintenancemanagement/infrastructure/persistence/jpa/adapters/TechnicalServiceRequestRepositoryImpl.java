package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.TechnicalServiceRequestRepository;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.assemblers.TechnicalServiceRequestPersistenceAssembler;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.repositories.TechnicalServiceRequestPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class TechnicalServiceRequestRepositoryImpl implements TechnicalServiceRequestRepository {
    private final TechnicalServiceRequestPersistenceRepository technicalServiceRequestPersistenceRepository;

    public TechnicalServiceRequestRepositoryImpl(TechnicalServiceRequestPersistenceRepository technicalServiceRequestPersistenceRepository) {
        this.technicalServiceRequestPersistenceRepository = technicalServiceRequestPersistenceRepository;
    }

    @Override
    public List<TechnicalServiceRequest> findAllByOrganizationId(Long organizationId) {
        return technicalServiceRequestPersistenceRepository.findAllByOrganizationId(organizationId).stream()
                .map(TechnicalServiceRequestPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<TechnicalServiceRequest> findByIdAndOrganizationId(Long id, Long organizationId) {
        return technicalServiceRequestPersistenceRepository.findByIdAndOrganizationId(id, organizationId)
                .map(TechnicalServiceRequestPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public TechnicalServiceRequest save(TechnicalServiceRequest request) {
        if (request.getId() == null) {
            return TechnicalServiceRequestPersistenceAssembler.toDomainFromPersistence(
                    technicalServiceRequestPersistenceRepository.save(
                            TechnicalServiceRequestPersistenceAssembler.toPersistenceFromDomain(request)));
        }
        var entity = technicalServiceRequestPersistenceRepository.findById(request.getId())
                .orElseGet(() -> TechnicalServiceRequestPersistenceAssembler.toPersistenceFromDomain(request));
        TechnicalServiceRequestPersistenceAssembler.copyDomainState(request, entity);
        return TechnicalServiceRequestPersistenceAssembler.toDomainFromPersistence(
                technicalServiceRequestPersistenceRepository.save(entity));
    }
}
