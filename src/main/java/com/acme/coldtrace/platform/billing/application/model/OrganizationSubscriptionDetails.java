package com.acme.coldtrace.platform.billing.application.model;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;

import java.util.List;

/**
 * Read model returned by the organization subscription query.
 *
 * @param subscription current organization subscription
 * @param plan subscribed plan
 * @param usage current usage counters
 * @param entitlements computed plan entitlements
 * @since 1.0
 */
public record OrganizationSubscriptionDetails(
        OrganizationSubscription subscription,
        SubscriptionPlan plan,
        OrganizationSubscriptionUsage usage,
        List<OrganizationEntitlement> entitlements
) {
}
