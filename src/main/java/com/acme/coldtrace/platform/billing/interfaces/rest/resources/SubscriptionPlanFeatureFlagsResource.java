package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

/**
 * REST resource representing subscription plan feature flags.
 *
 * @param allowsExports whether export workflows are included
 * @param allowsMaintenance whether maintenance workflows are included
 * @param allowsAiGuidance whether AI incident guidance is included
 * @param allowsAiReportSummary whether AI report summaries are included
 * @since 1.0
 */
public record SubscriptionPlanFeatureFlagsResource(
        boolean allowsExports,
        boolean allowsMaintenance,
        boolean allowsAiGuidance,
        boolean allowsAiReportSummary
) {
}
