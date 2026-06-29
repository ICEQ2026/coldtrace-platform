package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.billing.domain.model.entities.BillingWebhookEvent;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities.BillingWebhookEventPersistenceEntity;

/**
 * Assembler that translates billing webhook events between domain and persistence models.
 *
 * @since 1.0
 */
public final class BillingWebhookEventPersistenceAssembler {
    private BillingWebhookEventPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain entity.
     *
     * @param entity persistence entity
     * @return billing webhook event domain entity
     */
    public static BillingWebhookEvent toDomainFromPersistence(BillingWebhookEventPersistenceEntity entity) {
        return new BillingWebhookEvent(
                entity.getId(),
                entity.getProvider(),
                entity.getEventId(),
                entity.getEventType(),
                entity.getStatus(),
                entity.getOrganizationId(),
                entity.getProviderCustomerId(),
                entity.getProviderSubscriptionId(),
                entity.getProcessedAt(),
                entity.getMetadata()
        );
    }

    /**
     * Converts a domain event into a new persistence entity.
     *
     * @param event billing webhook event
     * @return persistence entity
     */
    public static BillingWebhookEventPersistenceEntity toPersistenceFromDomain(BillingWebhookEvent event) {
        var entity = new BillingWebhookEventPersistenceEntity();
        entity.setId(event.getId());
        copyDomainState(event, entity);
        return entity;
    }

    /**
     * Copies domain state into a persistence entity.
     *
     * @param event source event
     * @param entity target persistence entity
     */
    public static void copyDomainState(
            BillingWebhookEvent event,
            BillingWebhookEventPersistenceEntity entity
    ) {
        entity.setProvider(event.getProvider());
        entity.setEventId(event.getEventId());
        entity.setEventType(event.getEventType());
        entity.setStatus(event.getStatus());
        entity.setOrganizationId(event.getOrganizationId());
        entity.setProviderCustomerId(event.getProviderCustomerId());
        entity.setProviderSubscriptionId(event.getProviderSubscriptionId());
        entity.setProcessedAt(event.getProcessedAt());
        entity.setMetadata(event.getMetadata());
    }
}
