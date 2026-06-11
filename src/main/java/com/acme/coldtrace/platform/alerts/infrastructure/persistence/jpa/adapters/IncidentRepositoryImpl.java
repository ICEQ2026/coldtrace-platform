package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.assemblers.IncidentPersistenceAssembler;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.repositories.IncidentPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the incident domain repository.
 *
 * @since 1.0
 */
@Repository
public class IncidentRepositoryImpl implements IncidentRepository {
    private final IncidentPersistenceRepository incidentPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public IncidentRepositoryImpl(
            IncidentPersistenceRepository incidentPersistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.incidentPersistenceRepository = incidentPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Incident> findAllByOrganizationId(Long organizationId) {
        return incidentPersistenceRepository.findAllByOrganizationId(organizationId).stream()
                .map(IncidentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Incident> findByIdAndOrganizationId(Long id, Long organizationId) {
        return incidentPersistenceRepository.findByIdAndOrganizationId(id, organizationId)
                .map(IncidentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByIdAndOrganizationId(Long id, Long organizationId) {
        return incidentPersistenceRepository.existsByIdAndOrganizationId(id, organizationId);
    }

    @Override
    public Incident save(Incident incident) {
        if (incident.getId() == null) {
            var entity = IncidentPersistenceAssembler.toPersistenceFromDomain(incident);
            var savedIncident = IncidentPersistenceAssembler.toDomainFromPersistence(
                    incidentPersistenceRepository.save(entity)
            );
            savedIncident.onOpened();
            savedIncident.domainEvents().forEach(eventPublisher::publishEvent);
            savedIncident.clearDomainEvents();
            return savedIncident;
        }

        var entity = incidentPersistenceRepository.findById(incident.getId())
                .orElseGet(() -> IncidentPersistenceAssembler.toPersistenceFromDomain(incident));
        IncidentPersistenceAssembler.copyDomainState(incident, entity);
        return IncidentPersistenceAssembler.toDomainFromPersistence(incidentPersistenceRepository.save(entity));
    }
}
