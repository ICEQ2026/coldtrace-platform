package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities.OrganizationSubscriptionPersistenceEntity;

/**
 * Assembler that translates organization subscriptions between domain and persistence models.
 *
 * @since 1.0
 */
public final class OrganizationSubscriptionPersistenceAssembler {
    private OrganizationSubscriptionPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity
     * @return organization subscription aggregate
     */
    public static OrganizationSubscription toDomainFromPersistence(OrganizationSubscriptionPersistenceEntity entity) {
        return new OrganizationSubscription(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getPlanCode(),
                entity.getStatus(),
                entity.getProvider(),
                entity.getProviderCustomerId(),
                entity.getProviderSubscriptionId(),
                entity.getCurrentPeriodStart(),
                entity.getCurrentPeriodEnd(),
                entity.getCancelAtPeriodEnd(),
                entity.getMetadata()
        );
    }

    /**
     * Converts a domain aggregate into a new persistence entity.
     *
     * @param subscription organization subscription aggregate
     * @return persistence entity
     */
    public static OrganizationSubscriptionPersistenceEntity toPersistenceFromDomain(
            OrganizationSubscription subscription
    ) {
        var entity = new OrganizationSubscriptionPersistenceEntity();
        entity.setId(subscription.getId());
        copyDomainState(subscription, entity);
        return entity;
    }

    /**
     * Copies domain state into an existing persistence entity.
     *
     * @param subscription source aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(
            OrganizationSubscription subscription,
            OrganizationSubscriptionPersistenceEntity entity
    ) {
        entity.setOrganizationId(subscription.getOrganizationId());
        entity.setPlanCode(subscription.getPlanCode());
        entity.setStatus(subscription.getStatus());
        entity.setProvider(subscription.getProvider());
        entity.setProviderCustomerId(subscription.getProviderCustomerId());
        entity.setProviderSubscriptionId(subscription.getProviderSubscriptionId());
        entity.setCurrentPeriodStart(subscription.getCurrentPeriodStart());
        entity.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
        entity.setCancelAtPeriodEnd(subscription.getCancelAtPeriodEnd());
        entity.setMetadata(subscription.getMetadata());
    }
}
