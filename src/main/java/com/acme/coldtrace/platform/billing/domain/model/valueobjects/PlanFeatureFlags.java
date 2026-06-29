package com.acme.coldtrace.platform.billing.domain.model.valueobjects;

/**
 * Feature flags exposed by a subscription plan.
 *
 * @param allowsExports whether export workflows are included
 * @param allowsMaintenance whether maintenance workflows are included
 * @param allowsAiGuidance whether AI incident guidance is included
 * @param allowsAiReportSummary whether AI report summaries are included
 * @since 1.0
 */
public record PlanFeatureFlags(
        boolean allowsExports,
        boolean allowsMaintenance,
        boolean allowsAiGuidance,
        boolean allowsAiReportSummary
) {
}
