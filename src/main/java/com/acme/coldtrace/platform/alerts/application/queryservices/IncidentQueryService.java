package com.acme.coldtrace.platform.alerts.application.queryservices;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetIncidentByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetIncidentsByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract providing read access to incidents.
 *
 * @since 1.0
 */
public interface IncidentQueryService {
    /**
     * Retrieves incidents by organization.
     *
     * @param query query object containing organization identifier
     * @return success with incident list or failure with query error
     */
    Result<List<Incident>, IncidentQueryFailure> handle(GetIncidentsByOrganizationIdQuery query);

    /**
     * Retrieves one incident by id and organization.
     *
     * @param query query object containing organization and incident identifiers
     * @return success with incident or failure with query error
     */
    Result<Incident, IncidentQueryFailure> handle(GetIncidentByIdAndOrganizationIdQuery query);
}
