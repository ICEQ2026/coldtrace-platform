package com.acme.coldtrace.platform.billing.interfaces.rest.resources;

/**
 * REST resource representing subscription plan usage limits.
 *
 * @param maxLocations maximum locations allowed
 * @param maxAssets maximum assets allowed
 * @param maxIotDevices maximum IoT devices allowed
 * @param maxUsers maximum users allowed
 * @param historyRetentionDays report and telemetry history retention window
 * @since 1.0
 */
public record SubscriptionPlanUsageLimitsResource(
        Integer maxLocations,
        Integer maxAssets,
        Integer maxIotDevices,
        Integer maxUsers,
        Integer historyRetentionDays
) {
}
