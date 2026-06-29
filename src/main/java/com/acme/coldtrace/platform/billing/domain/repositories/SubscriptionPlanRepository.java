package com.acme.coldtrace.platform.billing.domain.repositories;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for subscription plans.
 *
 * @since 1.0
 */
public interface SubscriptionPlanRepository {
    /**
     * Finds all active plans visible to clients.
     *
     * @return active plans ordered by monthly price
     */
    List<SubscriptionPlan> findAllActive();

    /**
     * Finds a plan by stable code.
     *
     * @param code stable plan code
     * @return subscription plan when found
     */
    Optional<SubscriptionPlan> findByCode(String code);

    /**
     * Finds a plan by Stripe price identifier.
     *
     * @param stripePriceId Stripe price identifier
     * @return subscription plan when found
     */
    Optional<SubscriptionPlan> findByStripePriceId(String stripePriceId);

    /**
     * Persists a subscription plan.
     *
     * @param subscriptionPlan plan aggregate
     * @return persisted plan
     */
    SubscriptionPlan save(SubscriptionPlan subscriptionPlan);
}
