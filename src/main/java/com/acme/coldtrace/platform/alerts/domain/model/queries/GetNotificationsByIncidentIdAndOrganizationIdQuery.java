package com.acme.coldtrace.platform.alerts.domain.model.queries;

/**
 * Query for retrieving notifications for one incident scoped by organization.
 *
 * @param organizationId organization identifier
 * @param incidentId incident identifier
 * @since 1.0
 */
public record GetNotificationsByIncidentIdAndOrganizationIdQuery(Long organizationId, Long incidentId) {
    public GetNotificationsByIncidentIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("alerts.incident.error.organizationId.invalid");
        }
        if (incidentId == null || incidentId <= 0) {
            throw new IllegalArgumentException("alerts.incident.error.incidentId.invalid");
        }
    }
}
