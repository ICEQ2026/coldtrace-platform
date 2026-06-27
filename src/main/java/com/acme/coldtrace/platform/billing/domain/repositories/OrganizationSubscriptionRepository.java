package com.acme.coldtrace.platform.billing.domain.repositories;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;

import java.util.Optional;

/**
 * Domain repository contract for organization subscriptions.
 *
 * @since 1.0
 */
public interface OrganizationSubscriptionRepository {
    /**
     * Finds the current subscription for an organization.
     *
     * @param organizationId organization identifier
     * @return subscription when found
     */
    Optional<OrganizationSubscription> findByOrganizationId(Long organizationId);

    /**
     * Persists an organization subscription.
     *
     * @param subscription organization subscription
     * @return persisted subscription
     */
    OrganizationSubscription save(OrganizationSubscription subscription);
}
