package com.acme.coldtrace.platform.billing.application.model;

/**
 * Current usage counters supported by available bounded contexts.
 *
 * @param locations current organization locations
 * @param assets current organization assets
 * @param iotDevices current organization IoT devices
 * @param users current organization users
 * @since 1.0
 */
public record OrganizationSubscriptionUsage(
        Integer locations,
        Integer assets,
        Integer iotDevices,
        Integer users
) {
}
