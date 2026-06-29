package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response resource for a provider-hosted customer portal session.
 *
 * @param provider billing provider
 * @param sessionId provider session identifier
 * @param portalUrl provider-hosted customer portal URL
 * @param organizationId organization identifier
 * @since 1.0
 */
@Schema(
        name = "BillingPortalSessionResponse",
        description = "Provider-hosted customer portal session redirect"
)
public record BillingPortalSessionResource(
        @Schema(description = "Billing provider", example = "STRIPE")
        String provider,

        @Schema(description = "Provider customer portal session identifier", example = "bps_test_a1B2c3")
        String sessionId,

        @Schema(description = "Temporary provider-hosted customer portal URL", example = "https://billing.stripe.com/p/session/test_a1B2c3")
        String portalUrl,

        @Schema(description = "Organization identifier", example = "1")
        Long organizationId
) {
}
