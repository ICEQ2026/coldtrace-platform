package com.acme.coldtrace.platform.billing.application.model;

/**
 * Application result returned after processing one billing webhook event.
 *
 * @param provider provider name
 * @param eventId provider event id
 * @param eventType provider event type
 * @param processingStatus local processing status
 * @param duplicate whether this event had already been processed
 * @param organizationId updated organization id when available
 * @param planCode resulting local plan code when updated
 * @param subscriptionStatus resulting local subscription status when updated
 * @since 1.0
 */
public record BillingWebhookProcessingResult(
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
