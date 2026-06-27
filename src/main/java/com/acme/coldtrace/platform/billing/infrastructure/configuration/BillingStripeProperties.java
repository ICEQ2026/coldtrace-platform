package com.acme.coldtrace.platform.billing.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Environment-driven Stripe checkout configuration.
 *
 * @param secretKey Stripe secret key used server-side
 * @param checkoutSuccessUrl frontend URL after successful checkout
 * @param checkoutCancelUrl frontend URL after canceled checkout
 * @since 1.0
 */
@ConfigurationProperties(prefix = "coldtrace.billing.stripe")
public record BillingStripeProperties(
        String secretKey,
        String checkoutSuccessUrl,
        String checkoutCancelUrl
) {
    /**
     * Normalizes optional provider settings.
     */
    public BillingStripeProperties {
        secretKey = normalizeOptionalText(secretKey);
        checkoutSuccessUrl = normalizeOptionalText(checkoutSuccessUrl);
        checkoutCancelUrl = normalizeOptionalText(checkoutCancelUrl);
    }

    /**
     * @return true when the provider can create checkout sessions
     */
    public boolean hasCheckoutConfiguration() {
        return secretKey != null && checkoutSuccessUrl != null && checkoutCancelUrl != null;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
