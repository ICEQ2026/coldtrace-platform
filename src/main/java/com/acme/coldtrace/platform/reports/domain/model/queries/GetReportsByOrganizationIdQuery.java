package com.acme.coldtrace.platform.reports.domain.model.queries;

/**
 * Query for retrieving reports that belong to an organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetReportsByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     *
     * @throws IllegalArgumentException when the organization identifier is missing or invalid
     */
    public GetReportsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("reports.report.error.organizationId.invalid");
        }
    }
}
