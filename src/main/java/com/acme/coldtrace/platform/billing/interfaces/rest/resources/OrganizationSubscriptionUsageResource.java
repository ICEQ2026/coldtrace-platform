package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

/**
 * REST resource representing current usage counters for subscription limits.
 *
 * @param locations current organization locations
 * @param assets current organization assets
 * @param iotDevices current organization IoT devices
 * @param users current organization users
 * @since 1.0
 */
public record OrganizationSubscriptionUsageResource(
        Integer locations,
        Integer assets,
        Integer iotDevices,
        Integer users
) {
}
