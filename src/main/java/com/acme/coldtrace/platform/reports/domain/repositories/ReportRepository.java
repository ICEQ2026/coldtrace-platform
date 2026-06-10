package com.acme.coldtrace.platform.reports.domain.repositories;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for report aggregates.
 *
 * @since 1.0
 */
public interface ReportRepository {
    /**
     * Finds all reports owned by an organization.
     *
     * @param organizationId organization identifier
     * @return organization reports ordered by most recent first
     */
    List<Report> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one report by id and organization.
     *
     * @param id report identifier
     * @param organizationId organization identifier
     * @return report when found
     */
    Optional<Report> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Persists a report aggregate.
     *
     * @param report generated report
     * @return persisted report rebuilt from persistence state
     */
    Report save(Report report);
}
