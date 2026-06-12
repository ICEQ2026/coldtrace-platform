package com.acme.coldtrace.platform.alerts.domain.model.queries;

/**
 * Query for retrieving incident notifications owned by an organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetNotificationsByOrganizationIdQuery(Long organizationId) {
    public GetNotificationsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("alerts.incident.error.organizationId.invalid");
        }
    }
}
