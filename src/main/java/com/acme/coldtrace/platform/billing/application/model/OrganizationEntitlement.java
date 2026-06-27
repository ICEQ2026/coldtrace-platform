package com.acme.coldtrace.platform.billing.application.model;

/**
 * Computed entitlement exposed to clients and internal application services.
 *
 * @param key stable entitlement key
 * @param category entitlement category
 * @param enabled whether the organization can use the entitlement now
 * @param limit numeric limit when applicable
 * @param used current usage when available
 * @param remaining remaining capacity when applicable
 * @param lockedReason explanation when the entitlement is disabled
 * @since 1.0
 */
public record OrganizationEntitlement(
        String key,
        String category,
        Boolean enabled,
        Integer limit,
        Integer used,
        Integer remaining,
        String lockedReason
) {
}
