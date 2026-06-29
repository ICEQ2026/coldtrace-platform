package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.repositories.OrganizationSubscriptionRepository;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.assemblers.OrganizationSubscriptionPersistenceAssembler;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.repositories.OrganizationSubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-backed adapter for the organization subscription domain repository.
 *
 * @since 1.0
 */
@Repository
public class OrganizationSubscriptionRepositoryImpl implements OrganizationSubscriptionRepository {
    private final OrganizationSubscriptionPersistenceRepository organizationSubscriptionPersistenceRepository;

    public OrganizationSubscriptionRepositoryImpl(
            OrganizationSubscriptionPersistenceRepository organizationSubscriptionPersistenceRepository
    ) {
        this.organizationSubscriptionPersistenceRepository = organizationSubscriptionPersistenceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OrganizationSubscription> findByOrganizationId(Long organizationId) {
        return organizationSubscriptionPersistenceRepository.findByOrganizationId(organizationId)
                .map(OrganizationSubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OrganizationSubscription> findByProviderCustomerId(String providerCustomerId) {
        return organizationSubscriptionPersistenceRepository.findByProviderCustomerId(providerCustomerId)
                .map(OrganizationSubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OrganizationSubscription> findByProviderSubscriptionId(String providerSubscriptionId) {
        return organizationSubscriptionPersistenceRepository.findByProviderSubscriptionId(providerSubscriptionId)
                .map(OrganizationSubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationSubscription save(OrganizationSubscription subscription) {
        var entity = subscription.getId() == null
                ? organizationSubscriptionPersistenceRepository.findByOrganizationId(subscription.getOrganizationId())
                .orElseGet(() ->
                        OrganizationSubscriptionPersistenceAssembler.toPersistenceFromDomain(subscription))
                : organizationSubscriptionPersistenceRepository.findById(subscription.getId())
                .orElseGet(() ->
                        OrganizationSubscriptionPersistenceAssembler.toPersistenceFromDomain(subscription));
        OrganizationSubscriptionPersistenceAssembler.copyDomainState(subscription, entity);
        return OrganizationSubscriptionPersistenceAssembler.toDomainFromPersistence(
                organizationSubscriptionPersistenceRepository.save(entity)
        );
    }
}
