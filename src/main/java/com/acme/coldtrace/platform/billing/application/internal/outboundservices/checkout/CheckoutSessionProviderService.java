package com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout;

import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Outbound service for provider-hosted checkout sessions.
 *
 * @since 1.0
 */
public interface CheckoutSessionProviderService {
    /**
     * Creates a subscription checkout session with the configured provider.
     *
     * @param request provider request
     * @return provider session or controlled provider failure
     */
    Result<CheckoutSessionProviderResult, CheckoutSessionProviderFailure> createSubscriptionCheckoutSession(
            CheckoutSessionProviderRequest request
    );
}
