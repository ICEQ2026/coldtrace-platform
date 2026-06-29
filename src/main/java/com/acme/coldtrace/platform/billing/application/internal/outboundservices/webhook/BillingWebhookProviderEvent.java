package com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.SubscriptionStatus;

import java.time.OffsetDateTime;

/**
 * Normalized provider webhook event used by the billing application service.
 *
 * @param provider provider name
 * @param eventId provider event id
 * @param eventType provider event type
 * @param objectId provider object id
 * @param organizationId organization id from trusted provider metadata when present
 * @param providerCustomerId provider customer id
 * @param providerSubscriptionId provider subscription id
 * @param targetPlanCode plan code from trusted provider metadata when present
 * @param stripePriceId Stripe price id when available
 * @param subscriptionStatus subscription status implied by the event
 * @param currentPeriodStart current subscription period start
 * @param currentPeriodEnd current subscription period end
 * @param cancelAtPeriodEnd provider cancellation flag
 * @param supported whether ColdTrace handles this event type
 * @since 1.0
 */
public record BillingWebhookProviderEvent(
        String provider,
        String eventId,
        String eventType,
        String objectId,
        Long organizationId,
        String providerCustomerId,
        String providerSubscriptionId,
        String targetPlanCode,
        String stripePriceId,
        SubscriptionStatus subscriptionStatus,
        OffsetDateTime currentPeriodStart,
        OffsetDateTime currentPeriodEnd,
        Boolean cancelAtPeriodEnd,
        Boolean supported
) {
}
