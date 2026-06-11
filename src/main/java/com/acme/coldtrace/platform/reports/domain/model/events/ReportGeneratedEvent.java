package com.acme.coldtrace.platform.reports.domain.model.events;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;

import java.time.OffsetDateTime;

/**
 * Domain event raised when a report is generated.
 *
 * @param reportId report identifier
 * @param organizationId owning organization identifier
 * @param uuid external report UUID
 * @param type report type
 * @param generatedAt report generation timestamp
 * @since 1.0
 */
public record ReportGeneratedEvent(
        Long reportId,
        Long organizationId,
        String uuid,
        String type,
        OffsetDateTime generatedAt
) {
    /**
     * Builds the event from a report aggregate.
     *
     * @param report source aggregate
     * @return report-generated event
     */
    public static ReportGeneratedEvent from(Report report) {
        return new ReportGeneratedEvent(
                report.getId(),
                report.getOrganizationId(),
                report.getUuid(),
                report.getType(),
                report.getGeneratedAt()
        );
    }
}
