package com.acme.coldtrace.platform.aiassistance.interfaces.rest.transform;

import com.acme.coldtrace.platform.aiassistance.application.model.DashboardAiInterpretation;
import com.acme.coldtrace.platform.aiassistance.application.model.DashboardInsightDraft;
import com.acme.coldtrace.platform.aiassistance.application.model.DashboardSourceMetric;
import com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources.DashboardAiInterpretationInsightResource;
import com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources.DashboardAiInterpretationResource;
import com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources.DashboardAiSourceMetricResource;

import java.util.List;

/**
 * Assembler that converts generated dashboard interpretations into REST resources.
 *
 * @since 1.0
 */
public final class DashboardAiInterpretationResourceFromResultAssembler {
    private DashboardAiInterpretationResourceFromResultAssembler() {
    }

    /**
     * Converts a generated interpretation into a renderable REST resource.
     *
     * @param result generated dashboard interpretation
     * @return REST resource with advisory sections and source metrics
     */
    public static DashboardAiInterpretationResource toResourceFromResult(DashboardAiInterpretation result) {
        var interpretation = result.interpretation();
        return new DashboardAiInterpretationResource(
                result.organizationId(),
                result.question(),
                result.generatedAt(),
                interpretation.summary(),
                interpretation.attentionLevel(),
                safeList(interpretation.insights()).stream()
                        .map(DashboardAiInterpretationResourceFromResultAssembler::toInsightResource)
                        .toList(),
                safeList(interpretation.risks()),
                safeList(interpretation.recommendedActions()),
                safeList(interpretation.uncertaintyNotes()),
                safeList(result.sourceMetrics()).stream()
                        .map(DashboardAiInterpretationResourceFromResultAssembler::toSourceMetricResource)
                        .toList(),
                result.modelProvider(),
                result.modelName()
        );
    }

    private static DashboardAiInterpretationInsightResource toInsightResource(DashboardInsightDraft insight) {
        return new DashboardAiInterpretationInsightResource(
                insight.title(),
                insight.metric(),
                insight.interpretation(),
                insight.severity()
        );
    }

    private static DashboardAiSourceMetricResource toSourceMetricResource(DashboardSourceMetric sourceMetric) {
        return new DashboardAiSourceMetricResource(
                sourceMetric.name(),
                sourceMetric.value(),
                sourceMetric.unit(),
                sourceMetric.description()
        );
    }

    private static <T> List<T> safeList(List<T> source) {
        return source == null ? List.of() : source;
    }
}
