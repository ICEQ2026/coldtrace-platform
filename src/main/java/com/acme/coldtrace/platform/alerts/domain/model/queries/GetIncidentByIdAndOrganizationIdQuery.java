package com.acme.coldtrace.platform.alerts.domain.model.queries;

/**
 * Query for retrieving one incident by id and organization.
 *
 * @param organizationId organization identifier
 * @param incidentId incident identifier
 * @since 1.0
 */
public record GetIncidentByIdAndOrganizationIdQuery(Long organizationId, Long incidentId) {
    public GetIncidentByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("alerts.incident.error.organizationId.invalid");
        }
        if (incidentId == null || incidentId <= 0) {
            throw new IllegalArgumentException("alerts.incident.error.incidentId.invalid");
        }
    }
}
