package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.TechnicalServiceRequestRepository;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.assemblers.TechnicalServiceRequestPersistenceAssembler;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.repositories.TechnicalServiceRequestPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the technical service request domain repository.
 * <p>
 * The adapter keeps JPA entities behind the repository boundary and publishes
 * request-created domain events after persistence assigns the identifier used
 * by downstream integration listeners.
 *
 * @since 1.0
 */
@Repository
public class TechnicalServiceRequestRepositoryImpl implements TechnicalServiceRequestRepository {
    private final TechnicalServiceRequestPersistenceRepository technicalServiceRequestPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TechnicalServiceRequestRepositoryImpl(
            TechnicalServiceRequestPersistenceRepository technicalServiceRequestPersistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.technicalServiceRequestPersistenceRepository = technicalServiceRequestPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TechnicalServiceRequest> findAllByOrganizationId(Long organizationId) {
        return technicalServiceRequestPersistenceRepository.findAllByOrganizationIdOrderByRequestedAtDesc(organizationId)
                .stream()
                .map(TechnicalServiceRequestPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<TechnicalServiceRequest> findByIdAndOrganizationId(Long id, Long organizationId) {
        return technicalServiceRequestPersistenceRepository.findByIdAndOrganizationId(id, organizationId)
                .map(TechnicalServiceRequestPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TechnicalServiceRequest save(TechnicalServiceRequest request) {
        if (request.getId() == null) {
            var savedRequest = TechnicalServiceRequestPersistenceAssembler.toDomainFromPersistence(
                    technicalServiceRequestPersistenceRepository.save(
                            TechnicalServiceRequestPersistenceAssembler.toPersistenceFromDomain(request)
                    )
            );
            savedRequest.onCreated();
            savedRequest.domainEvents().forEach(eventPublisher::publishEvent);
            savedRequest.clearDomainEvents();
            return savedRequest;
        }

        var entity = technicalServiceRequestPersistenceRepository.findById(request.getId())
                .orElseGet(() -> TechnicalServiceRequestPersistenceAssembler.toPersistenceFromDomain(request));
        TechnicalServiceRequestPersistenceAssembler.copyDomainState(request, entity);
        return TechnicalServiceRequestPersistenceAssembler.toDomainFromPersistence(
                technicalServiceRequestPersistenceRepository.save(entity)
        );
    }
}
