package com.acme.coldtrace.platform.reports.domain.model.queries;

/**
 * Query for retrieving one report inside an organization.
 *
 * @param organizationId organization identifier
 * @param reportId report identifier
 * @since 1.0
 */
public record GetReportByIdAndOrganizationIdQuery(Long organizationId, Long reportId) {
    /**
     * Validates query identifiers.
     *
     * @throws IllegalArgumentException when any identifier is missing or invalid
     */
    public GetReportByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("reports.report.error.organizationId.invalid");
        }
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("reports.report.error.reportId.invalid");
        }
    }
}
