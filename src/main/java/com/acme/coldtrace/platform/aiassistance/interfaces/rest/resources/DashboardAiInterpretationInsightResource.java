package com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources;

/**
 * REST resource representing one dashboard AI metric insight.
 *
 * @param title short insight title
 * @param metric source metric or operational area
 * @param interpretation advisory interpretation
 * @param severity business severity label
 * @since 1.0
 */
public record DashboardAiInterpretationInsightResource(
        String title,
        String metric,
        String interpretation,
        String severity
) {
}
