package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.assemblers.SubscriptionPlanPersistenceAssembler;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.repositories.SubscriptionPlanPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the subscription plan domain repository.
 *
 * @since 1.0
 */
@Repository
public class SubscriptionPlanRepositoryImpl implements SubscriptionPlanRepository {
    private final SubscriptionPlanPersistenceRepository subscriptionPlanPersistenceRepository;

    public SubscriptionPlanRepositoryImpl(SubscriptionPlanPersistenceRepository subscriptionPlanPersistenceRepository) {
        this.subscriptionPlanPersistenceRepository = subscriptionPlanPersistenceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SubscriptionPlan> findAllActive() {
        return subscriptionPlanPersistenceRepository.findAllByActiveTrueOrderByMonthlyPriceCentsAsc().stream()
                .map(SubscriptionPlanPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SubscriptionPlan> findByCode(String code) {
        return subscriptionPlanPersistenceRepository.findByCode(code)
                .map(SubscriptionPlanPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubscriptionPlan save(SubscriptionPlan subscriptionPlan) {
        if (subscriptionPlan.getId() == null) {
            var entity = SubscriptionPlanPersistenceAssembler.toPersistenceFromDomain(subscriptionPlan);
            return SubscriptionPlanPersistenceAssembler.toDomainFromPersistence(
                    subscriptionPlanPersistenceRepository.save(entity)
            );
        }

        var entity = subscriptionPlanPersistenceRepository.findById(subscriptionPlan.getId())
                .orElseGet(() -> SubscriptionPlanPersistenceAssembler.toPersistenceFromDomain(subscriptionPlan));
        SubscriptionPlanPersistenceAssembler.copyDomainState(subscriptionPlan, entity);
        return SubscriptionPlanPersistenceAssembler.toDomainFromPersistence(
                subscriptionPlanPersistenceRepository.save(entity)
        );
    }
}
