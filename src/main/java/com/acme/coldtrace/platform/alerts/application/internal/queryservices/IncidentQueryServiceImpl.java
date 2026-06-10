package com.acme.coldtrace.platform.alerts.application.internal.queryservices;

import com.acme.coldtrace.platform.alerts.application.queryservices.IncidentQueryFailure;
import com.acme.coldtrace.platform.alerts.application.queryservices.IncidentQueryService;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetIncidentByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetIncidentsByOrganizationIdQuery;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for incident query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class IncidentQueryServiceImpl implements IncidentQueryService {
    private final IncidentRepository incidentRepository;
    private final OrganizationRepository organizationRepository;

    public IncidentQueryServiceImpl(
            IncidentRepository incidentRepository,
            OrganizationRepository organizationRepository
    ) {
        this.incidentRepository = incidentRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Retrieves incidents from persistence by organization.
     *
     * @param query query object containing organization identifier
     * @return success with incident list or failure with query error
     */
    @Override
    public Result<List<Incident>, IncidentQueryFailure> handle(GetIncidentsByOrganizationIdQuery query) {
        if (!organizationRepository.existsById(query.organizationId())) {
            log.warn("Organization not found for incident query: organizationId={}", query.organizationId());
            return Result.failure(new IncidentQueryFailure.OrganizationNotFound());
        }
        var incidents = incidentRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} incidents for organizationId={}", incidents.size(), query.organizationId());
        return Result.success(incidents);
    }

    /**
     * Retrieves one incident from persistence by id and organization.
     *
     * @param query query object containing organization and incident identifiers
     * @return success with incident or failure with query error
     */
    @Override
    public Result<Incident, IncidentQueryFailure> handle(GetIncidentByIdAndOrganizationIdQuery query) {
        if (!organizationRepository.existsById(query.organizationId())) {
            log.warn("Organization not found for incident detail query: organizationId={}", query.organizationId());
            return Result.failure(new IncidentQueryFailure.OrganizationNotFound());
        }
        var incident = incidentRepository.findByIdAndOrganizationId(query.incidentId(), query.organizationId());
        if (incident.isEmpty()) {
            log.warn("Incident not found: organizationId={}, incidentId={}",
                    query.organizationId(), query.incidentId());
            return Result.failure(new IncidentQueryFailure.IncidentNotFound());
        }
        return Result.success(incident.get());
    }
}
