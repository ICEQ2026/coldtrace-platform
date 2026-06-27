package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response resource for a provider-hosted checkout session.
 *
 * @param provider billing provider
 * @param sessionId provider session identifier
 * @param checkoutUrl provider-hosted checkout URL
 * @param targetPlanCode target paid plan code
 * @since 1.0
 */
@Schema(
        name = "BillingCheckoutSessionResponse",
        description = "Provider-hosted checkout session redirect"
)
public record BillingCheckoutSessionResource(
        @Schema(description = "Billing provider", example = "STRIPE")
        String provider,

        @Schema(description = "Provider checkout session identifier", example = "cs_test_a1B2c3")
        String sessionId,

        @Schema(description = "Temporary provider-hosted checkout URL", example = "https://checkout.stripe.com/c/pay/cs_test_a1B2c3")
        String checkoutUrl,

        @Schema(description = "Target paid subscription plan code", example = "operations")
        String targetPlanCode
) {
}
