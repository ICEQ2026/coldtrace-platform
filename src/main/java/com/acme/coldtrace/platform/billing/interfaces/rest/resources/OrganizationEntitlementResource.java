package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

/**
 * REST resource representing one computed organization entitlement.
 *
 * @param key stable entitlement key
 * @param category entitlement category
 * @param enabled whether the entitlement is currently available
 * @param limit numeric limit when applicable
 * @param used current usage when available
 * @param remaining remaining capacity when applicable
 * @param lockedReason explanation when disabled
 * @since 1.0
 */
public record OrganizationEntitlementResource(
        String key,
        String category,
        Boolean enabled,
        Integer limit,
        Integer used,
        Integer remaining,
        String lockedReason
) {
}
