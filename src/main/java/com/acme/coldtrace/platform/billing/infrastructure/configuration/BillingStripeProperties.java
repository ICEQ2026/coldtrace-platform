package com.acme.coldtrace.platform.billing.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Environment-driven Stripe checkout configuration.
 *
 * @param secretKey Stripe secret key used server-side
 * @param webhookSigningSecret Stripe webhook endpoint signing secret
 * @param checkoutSuccessUrl frontend URL after successful checkout
 * @param checkoutCancelUrl frontend URL after canceled checkout
 * @param customerPortalReturnUrl frontend URL after leaving Stripe Customer Portal
 * @since 1.0
 */
@ConfigurationProperties(prefix = "coldtrace.billing.stripe")
public record BillingStripeProperties(
        String secretKey,
        String webhookSigningSecret,
        String checkoutSuccessUrl,
        String checkoutCancelUrl,
        String customerPortalReturnUrl
) {
    /**
     * Normalizes optional provider settings.
     */
    public BillingStripeProperties {
        secretKey = normalizeOptionalText(secretKey);
        webhookSigningSecret = normalizeOptionalText(webhookSigningSecret);
        checkoutSuccessUrl = normalizeOptionalText(checkoutSuccessUrl);
        checkoutCancelUrl = normalizeOptionalText(checkoutCancelUrl);
        customerPortalReturnUrl = normalizeOptionalText(customerPortalReturnUrl);
    }

    /**
     * @return true when the provider can create checkout sessions
     */
    public boolean hasCheckoutConfiguration() {
        return secretKey != null && checkoutSuccessUrl != null && checkoutCancelUrl != null;
    }

    /**
     * @return true when signed Stripe webhook processing can be enabled
     */
    public boolean hasWebhookConfiguration() {
        return webhookSigningSecret != null;
    }

    /**
     * @return true when the provider can create customer portal sessions
     */
    public boolean hasCustomerPortalConfiguration() {
        return secretKey != null && customerPortalReturnUrl != null;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
