package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST resource representing an organization's current subscription.
 *
 * @param id subscription identifier
 * @param organizationId organization identifier
 * @param status subscription status
 * @param provider billing provider
 * @param providerCustomerId optional provider customer identifier
 * @param providerSubscriptionId optional provider subscription identifier
 * @param currentPeriodStart optional billing period start
 * @param currentPeriodEnd optional billing period end
 * @param cancelAtPeriodEnd whether the subscription is scheduled to cancel
 * @param metadata optional billing metadata
 * @param plan subscribed plan
 * @param usage current supported usage counters
 * @param entitlements computed entitlements
 * @since 1.0
 */
public record OrganizationSubscriptionResource(
        Long id,
        Long organizationId,
        String status,
        String provider,
        String providerCustomerId,
        String providerSubscriptionId,
        OffsetDateTime currentPeriodStart,
        OffsetDateTime currentPeriodEnd,
        Boolean cancelAtPeriodEnd,
        String metadata,
        SubscriptionPlanResource plan,
        OrganizationSubscriptionUsageResource usage,
        List<OrganizationEntitlementResource> entitlements
) {
}
