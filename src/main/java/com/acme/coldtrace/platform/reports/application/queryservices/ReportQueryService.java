package com.acme.coldtrace.platform.reports.application.queryservices;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.model.queries.GetReportByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.reports.domain.model.queries.GetReportsByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for report query operations.
 *
 * @since 1.0
 */
public interface ReportQueryService {
    /**
     * Retrieves reports for an organization.
     *
     * @param query query containing the organization identifier
     * @return success with reports or failure with query error
     */
    Result<List<Report>, ReportQueryFailure> handle(GetReportsByOrganizationIdQuery query);

    /**
     * Retrieves one report by id and organization.
     *
     * @param query query containing report and organization identifiers
     * @return success with report or failure with query error
     */
    Result<Report, ReportQueryFailure> handle(GetReportByIdAndOrganizationIdQuery query);
}
