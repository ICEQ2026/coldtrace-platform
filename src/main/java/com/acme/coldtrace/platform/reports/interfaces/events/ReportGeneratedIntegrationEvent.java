package com.acme.coldtrace.platform.reports.interfaces.events;

import java.time.OffsetDateTime;

/**
 * Integration event published by reports when an operational report is generated.
 *
 * @param reportId report identifier
 * @param organizationId owning organization identifier
 * @param uuid external report UUID
 * @param type report type
 * @param generatedAt report generation timestamp
 * @since 1.0
 */
public record ReportGeneratedIntegrationEvent(
        Long reportId,
        Long organizationId,
        String uuid,
        String type,
        OffsetDateTime generatedAt
) {
}
