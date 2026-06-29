package com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal;

import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Outbound service for provider-hosted customer portal sessions.
 *
 * @since 1.0
 */
public interface PortalSessionProviderService {
    /**
     * Creates a customer portal session with the configured provider.
     *
     * @param request provider request
     * @return provider session or controlled provider failure
     */
    Result<PortalSessionProviderResult, PortalSessionProviderFailure> createCustomerPortalSession(
            PortalSessionProviderRequest request
    );
}
