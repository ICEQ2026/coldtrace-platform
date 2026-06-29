package com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal;

/**
 * Customer portal session created by an external provider.
 *
 * @param provider provider code
 * @param sessionId provider session id
 * @param portalUrl provider-hosted customer portal URL
 * @since 1.0
 */
public record PortalSessionProviderResult(
        String provider,
        String sessionId,
        String portalUrl
) {
}
