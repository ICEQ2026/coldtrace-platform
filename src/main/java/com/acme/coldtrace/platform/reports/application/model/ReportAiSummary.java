package com.acme.coldtrace.platform.reports.application.model;

import com.acme.coldtrace.platform.aiassistance.application.model.ComplianceSummaryDraft;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;

import java.time.OffsetDateTime;

/**
 * Advisory AI summary generated from a persisted report and related evidence.
 *
 * @param report source report used as factual input
 * @param summary structured advisory summary
 * @param modelProvider configured AI provider
 * @param modelName configured AI model
 * @param generatedAt generation timestamp
 * @since 1.0
 */
public record ReportAiSummary(
        Report report,
        ComplianceSummaryDraft summary,
        String modelProvider,
        String modelName,
        OffsetDateTime generatedAt
) {
}
