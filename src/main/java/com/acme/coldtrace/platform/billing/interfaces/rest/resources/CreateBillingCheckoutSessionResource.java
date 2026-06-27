package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource for creating a provider-hosted checkout session.
 *
 * @param targetPlanCode target paid plan code
 * @since 1.0
 */
@Schema(
        name = "CreateBillingCheckoutSessionRequest",
        description = "Billing checkout session creation request"
)
public record CreateBillingCheckoutSessionResource(
        @NotBlank(message = "is required")
        @Schema(description = "Target paid subscription plan code", example = "operations")
        String targetPlanCode
) {
}
