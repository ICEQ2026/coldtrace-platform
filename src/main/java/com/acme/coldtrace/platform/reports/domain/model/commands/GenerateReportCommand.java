package com.acme.coldtrace.platform.reports.domain.model.commands;

import java.time.OffsetDateTime;

/**
 * Command for generating an organization report.
 * <p>
 * Reports are backend-generated snapshots built from persisted operational data.
 * The command defines the organization, report type and inclusive date range to
 * summarize. Application services validate the organization and collect readings
 * and incidents before creating the report aggregate.
 *
 * @param organizationId organization identifier from the route
 * @param type report type requested by the client
 * @param title human-readable report title
 * @param periodStart inclusive lower bound for operational data
 * @param periodEnd inclusive upper bound for operational data
 * @since 1.0
 */
public record GenerateReportCommand(
        Long organizationId,
        String type,
        String title,
        OffsetDateTime periodStart,
        OffsetDateTime periodEnd
) {
    /**
     * Validates and normalizes report generation data.
     *
     * @throws IllegalArgumentException when required fields are missing or the range is invalid
     */
    public GenerateReportCommand {
        organizationId = requirePositive(organizationId, "reports.report.error.organizationId.invalid");
        type = requireNonBlank(type, "reports.report.error.type.required").toUpperCase();
        title = requireNonBlank(title, "reports.report.error.title.required");
        if (periodStart == null) {
            throw new IllegalArgumentException("reports.report.error.periodStart.required");
        }
        if (periodEnd == null) {
            throw new IllegalArgumentException("reports.report.error.periodEnd.required");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("reports.report.error.date-range.invalid");
        }
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
