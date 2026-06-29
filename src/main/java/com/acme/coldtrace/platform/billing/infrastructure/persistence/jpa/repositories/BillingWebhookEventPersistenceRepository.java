package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities.BillingWebhookEventPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for billing webhook event persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface BillingWebhookEventPersistenceRepository
        extends JpaRepository<BillingWebhookEventPersistenceEntity, Long> {
    /**
     * Checks if an event was already processed.
     *
     * @param provider billing provider
     * @param eventId provider event id
     * @return true when a stored event exists
     */
    boolean existsByProviderAndEventId(BillingProvider provider, String eventId);
}
