package com.acme.coldtrace.platform.alerts.domain.model.queries;

/**
 * Query for retrieving incidents owned by an organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetIncidentsByOrganizationIdQuery(Long organizationId) {
    public GetIncidentsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("alerts.incident.error.organizationId.invalid");
        }
    }
}
