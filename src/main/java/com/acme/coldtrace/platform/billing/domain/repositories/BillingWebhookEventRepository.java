package com.acme.coldtrace.platform.billing.domain.repositories;

import com.acme.coldtrace.platform.billing.domain.model.entities.BillingWebhookEvent;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;

/**
 * Domain repository contract for billing webhook event idempotency records.
 *
 * @since 1.0
 */
public interface BillingWebhookEventRepository {
    /**
     * Checks whether a provider event was already handled.
     *
     * @param provider billing provider
     * @param eventId provider event identifier
     * @return true when a stored idempotency record exists
     */
    boolean existsByProviderAndEventId(BillingProvider provider, String eventId);

    /**
     * Persists a processed webhook event record.
     *
     * @param event processed event
     * @return persisted event
     */
    BillingWebhookEvent save(BillingWebhookEvent event);
}
