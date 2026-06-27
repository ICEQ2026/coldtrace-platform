package com.acme.coldtrace.platform.billing.interfaces.acl;

import java.util.List;
import java.util.Optional;

/**
 * Published anti-corruption facade for subscription and entitlement checks.
 *
 * @since 1.0
 */
public interface SubscriptionBillingContextFacade {
    /**
     * Ensures a newly created organization has its initial Base subscription.
     *
     * @param organizationId organization identifier
     */
    void initializeBaseSubscriptionForOrganization(Long organizationId);

    /**
     * Fetches the current entitlement snapshot for an organization.
     *
     * @param organizationId organization identifier
     * @return entitlement snapshot when the organization and subscription plan exist
     */
    Optional<OrganizationEntitlementsSnapshot> fetchEntitlementsByOrganizationId(Long organizationId);

    /**
     * Checks whether an organization can use a specific entitlement now.
     *
     * @param organizationId organization identifier
     * @param entitlementKey stable entitlement key
     * @return true when the entitlement is enabled
     */
    boolean canUseEntitlement(Long organizationId, String entitlementKey);

    /**
     * Subscription entitlement data published to other contexts.
     */
    record OrganizationEntitlementsSnapshot(
            Long organizationId,
            String planCode,
            String status,
            List<EntitlementItemSnapshot> entitlements
    ) {
    }

    /**
     * One entitlement item published to other contexts.
     */
    record EntitlementItemSnapshot(
            String key,
            String category,
            Boolean enabled,
            Integer limit,
            Integer used,
            Integer remaining,
            String lockedReason
    ) {
    }
}
