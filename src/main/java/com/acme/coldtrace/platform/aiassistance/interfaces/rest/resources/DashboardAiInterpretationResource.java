package com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST resource representing an advisory AI interpretation for the dashboard.
 *
 * @param organizationId organization that owns the source dashboard data
 * @param question optional operator question used by the generation
 * @param generatedAt generation timestamp
 * @param overallReading concise operational reading
 * @param attentionLevel operator-facing attention level
 * @param metricInsights structured metric-level insights
 * @param risks operational risks inferred from source metrics
 * @param recommendedActions advisory next actions
 * @param uncertaintyNotes assumptions or missing-context notes
 * @param sourceMetrics factual dashboard metrics used as source references
 * @param modelProvider configured AI provider used for this response
 * @param modelName configured AI model used for this response
 * @since 1.0
 */
public record DashboardAiInterpretationResource(
        Long organizationId,
        String question,
        OffsetDateTime generatedAt,
        String overallReading,
        String attentionLevel,
        List<DashboardAiInterpretationInsightResource> metricInsights,
        List<String> risks,
        List<String> recommendedActions,
        List<String> uncertaintyNotes,
        List<DashboardAiSourceMetricResource> sourceMetrics,
        String modelProvider,
        String modelName
) {
}
