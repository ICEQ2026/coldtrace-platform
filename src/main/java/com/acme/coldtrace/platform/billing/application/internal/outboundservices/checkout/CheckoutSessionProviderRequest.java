package com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout;

/**
 * Request sent to the external checkout provider adapter.
 *
 * @param organizationId organization requesting checkout
 * @param targetPlanCode target plan code
 * @param stripePriceId configured Stripe price id
 * @param providerCustomerId optional existing provider customer id
 * @since 1.0
 */
public record CheckoutSessionProviderRequest(
        Long organizationId,
        String targetPlanCode,
        String stripePriceId,
        String providerCustomerId
) {
}
