package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.billing.domain.model.entities.BillingWebhookEvent;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.repositories.BillingWebhookEventRepository;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.assemblers.BillingWebhookEventPersistenceAssembler;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.repositories.BillingWebhookEventPersistenceRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA-backed adapter for billing webhook event idempotency records.
 *
 * @since 1.0
 */
@Repository
public class BillingWebhookEventRepositoryImpl implements BillingWebhookEventRepository {
    private final BillingWebhookEventPersistenceRepository billingWebhookEventPersistenceRepository;

    public BillingWebhookEventRepositoryImpl(
            BillingWebhookEventPersistenceRepository billingWebhookEventPersistenceRepository
    ) {
        this.billingWebhookEventPersistenceRepository = billingWebhookEventPersistenceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByProviderAndEventId(BillingProvider provider, String eventId) {
        return billingWebhookEventPersistenceRepository.existsByProviderAndEventId(provider, eventId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BillingWebhookEvent save(BillingWebhookEvent event) {
        return BillingWebhookEventPersistenceAssembler.toDomainFromPersistence(
                billingWebhookEventPersistenceRepository.save(
                        BillingWebhookEventPersistenceAssembler.toPersistenceFromDomain(event)
                )
        );
    }
}
