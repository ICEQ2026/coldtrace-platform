package com.acme.coldtrace.platform.billing.application.model;

/**
 * Provider-hosted customer portal session returned to the frontend.
 *
 * @param provider billing provider that created the session
 * @param sessionId provider session identifier
 * @param portalUrl temporary redirect URL
 * @param organizationId organization that owns the billing portal session
 * @since 1.0
 */
public record BillingPortalSession(
        String provider,
        String sessionId,
        String portalUrl,
        Long organizationId
) {
}
