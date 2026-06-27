package com.acme.coldtrace.platform.billing.application.model;

/**
 * Provider-hosted checkout session returned to the frontend.
 *
 * @param provider billing provider that created the session
 * @param sessionId provider session identifier
 * @param checkoutUrl temporary redirect URL
 * @param targetPlanCode requested target plan code
 * @since 1.0
 */
public record BillingCheckoutSession(
        String provider,
        String sessionId,
        String checkoutUrl,
        String targetPlanCode
) {
}
