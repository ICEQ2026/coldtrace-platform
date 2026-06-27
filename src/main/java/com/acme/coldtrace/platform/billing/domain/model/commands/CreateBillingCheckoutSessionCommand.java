package com.acme.coldtrace.platform.billing.domain.model.commands;

import java.util.Locale;

/**
 * Command for creating a provider-hosted checkout session for a paid plan.
 *
 * @param organizationId organization requesting the upgrade
 * @param targetPlanCode target subscription plan code
 * @since 1.0
 */
public record CreateBillingCheckoutSessionCommand(
        Long organizationId,
        String targetPlanCode
) {
    public CreateBillingCheckoutSessionCommand {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("billing.checkout-session.error.organizationId.invalid");
        }
        if (targetPlanCode == null || targetPlanCode.isBlank()) {
            throw new IllegalArgumentException("billing.checkout-session.error.targetPlanCode.required");
        }
        targetPlanCode = targetPlanCode.trim().toLowerCase(Locale.ROOT);
    }
}
