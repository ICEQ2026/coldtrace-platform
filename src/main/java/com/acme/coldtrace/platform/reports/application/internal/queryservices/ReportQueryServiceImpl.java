package com.acme.coldtrace.platform.reports.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.reports.application.queryservices.ReportQueryFailure;
import com.acme.coldtrace.platform.reports.application.queryservices.ReportQueryService;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.model.queries.GetReportByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.reports.domain.model.queries.GetReportsByOrganizationIdQuery;
import com.acme.coldtrace.platform.reports.domain.repositories.ReportRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for report query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReportQueryServiceImpl implements ReportQueryService {
    private final ReportRepository reportRepository;
    private final OrganizationRepository organizationRepository;

    public ReportQueryServiceImpl(
            ReportRepository reportRepository,
            OrganizationRepository organizationRepository
    ) {
        this.reportRepository = reportRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Retrieves generated reports for an organization.
     *
     * @param query query containing the organization identifier
     * @return success with reports or failure with query error
     * @see GetReportsByOrganizationIdQuery
     */
    @Override
    public Result<List<Report>, ReportQueryFailure> handle(GetReportsByOrganizationIdQuery query) {
        if (!organizationRepository.existsById(query.organizationId())) {
            return Result.failure(new ReportQueryFailure.OrganizationNotFound());
        }
        return Result.success(reportRepository.findAllByOrganizationId(query.organizationId()));
    }

    /**
     * Retrieves one report by id and organization.
     *
     * @param query query containing report and organization identifiers
     * @return success with report or failure with query error
     * @see GetReportByIdAndOrganizationIdQuery
     */
    @Override
    public Result<Report, ReportQueryFailure> handle(GetReportByIdAndOrganizationIdQuery query) {
        if (!organizationRepository.existsById(query.organizationId())) {
            return Result.failure(new ReportQueryFailure.OrganizationNotFound());
        }
        var report = reportRepository.findByIdAndOrganizationId(query.reportId(), query.organizationId());
        if (report.isEmpty()) {
            return Result.failure(new ReportQueryFailure.ReportNotFound());
        }
        return Result.success(report.get());
    }
}
