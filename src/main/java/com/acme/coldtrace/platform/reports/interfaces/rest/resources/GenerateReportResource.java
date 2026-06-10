package com.acme.coldtrace.platform.reports.interfaces.rest.resources;

import java.time.OffsetDateTime;

/**
 * Request resource used to generate a report.
 *
 * @param type report type
 * @param title human-readable title
 * @param periodStart inclusive lower data bound
 * @param periodEnd inclusive upper data bound
 * @since 1.0
 */
public record GenerateReportResource(
        String type,
        String title,
        OffsetDateTime periodStart,
        OffsetDateTime periodEnd
) {
}
