package com.acme.coldtrace.platform.billing.domain.model.commands;

/**
 * Command for processing one signed Stripe webhook request.
 *
 * @param payload raw request body received from Stripe
 * @param signatureHeader value of the Stripe-Signature header
 * @since 1.0
 */
public record ProcessStripeWebhookCommand(
        String payload,
        String signatureHeader
) {
    /**
     * Normalizes request values without changing the raw payload.
     */
    public ProcessStripeWebhookCommand {
        signatureHeader = normalize(signatureHeader);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
