package com.acme.coldtrace.platform.alerts.application.acl;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application-layer implementation of {@link AlertsContextFacade}.
 *
 * @since 1.0
 */
@Service
public class AlertsContextFacadeImpl implements AlertsContextFacade {
    private final IncidentRepository incidentRepository;

    public AlertsContextFacadeImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IncidentSnapshot> fetchIncidentsByOrganizationId(Long organizationId) {
        return incidentRepository.findAllByOrganizationId(organizationId).stream()
                .map(this::toSnapshot)
                .toList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<IncidentSnapshot> fetchIncidentByIdAndOrganizationId(Long organizationId, Long incidentId) {
        return incidentRepository.findByIdAndOrganizationId(incidentId, organizationId)
                .map(this::toSnapshot);
    }
    private IncidentSnapshot toSnapshot(Incident incident) {
        return new IncidentSnapshot(
                incident.getId(),
                incident.getOrganizationId(),
                incident.getStatus().name(),
                incident.getDetectedAt()
        );
    }
}

