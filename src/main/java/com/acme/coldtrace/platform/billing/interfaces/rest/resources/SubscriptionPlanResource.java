package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

import java.util.List;

/**
 * REST resource representing a subscription plan.
 *
 * @param id plan identifier
 * @param code stable plan code
 * @param displayName user-facing plan name
 * @param description user-facing plan description
 * @param monthlyPriceCents monthly price in minor units
 * @param currency ISO currency code
 * @param stripePriceId optional Stripe price identifier
 * @param recommended whether this plan should be highlighted
 * @param recommendedLabel optional highlight label
 * @param visible whether this plan is visible/selectable by clients
 * @param usageLimits usage limit matrix
 * @param featureFlags feature entitlement flags
 * @param includedFeatures user-facing feature list
 * @since 1.0
 */
public record SubscriptionPlanResource(
        Long id,
        String code,
        String displayName,
        String description,
        Integer monthlyPriceCents,
        String currency,
        String stripePriceId,
        Boolean recommended,
        String recommendedLabel,
        Boolean visible,
        SubscriptionPlanUsageLimitsResource usageLimits,
        SubscriptionPlanFeatureFlagsResource featureFlags,
        List<String> includedFeatures
) {
}
