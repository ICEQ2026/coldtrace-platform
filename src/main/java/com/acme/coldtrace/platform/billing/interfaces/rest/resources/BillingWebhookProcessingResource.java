package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

/**
 * REST resource returned after processing a billing webhook.
 *
 * @param provider provider name
 * @param eventId provider event id
 * @param eventType provider event type
 * @param processingStatus local processing status
 * @param duplicate whether the event was already processed before this request
 * @param organizationId updated organization id when available
 * @param planCode resulting local plan code when updated
 * @param subscriptionStatus resulting local subscription status when updated
 * @since 1.0
 */
public record BillingWebhookProcessingResource(
        String provider,
        String eventId,
        String eventType,
        String processingStatus,
        Boolean duplicate,
        Long organizationId,
        String planCode,
        String subscriptionStatus
) {
}
