package com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout;

/**
 * Checkout session created by an external provider.
 *
 * @param provider provider code
 * @param sessionId provider session id
 * @param checkoutUrl provider-hosted checkout URL
 * @since 1.0
 */
public record CheckoutSessionProviderResult(
        String provider,
        String sessionId,
        String checkoutUrl
) {
}
