package com.acme.coldtrace.platform.billing.domain.model.valueobjects;

/**
 * Usage limits exposed by a subscription plan.
 *
 * @param maxLocations maximum locations allowed
 * @param maxAssets maximum assets allowed
 * @param maxIotDevices maximum IoT devices allowed
 * @param maxUsers maximum users allowed
 * @param historyRetentionDays report and telemetry history retention window
 * @since 1.0
 */
public record PlanUsageLimits(
        Integer maxLocations,
        Integer maxAssets,
        Integer maxIotDevices,
        Integer maxUsers,
        Integer historyRetentionDays
) {
    /**
     * Validates that configured limits are positive when present.
     */
    public PlanUsageLimits {
        maxLocations = requirePositiveOrNull(maxLocations, "maxLocations");
        maxAssets = requirePositiveOrNull(maxAssets, "maxAssets");
        maxIotDevices = requirePositiveOrNull(maxIotDevices, "maxIotDevices");
        maxUsers = requirePositiveOrNull(maxUsers, "maxUsers");
        historyRetentionDays = requirePositiveOrNull(historyRetentionDays, "historyRetentionDays");
    }

    private static Integer requirePositiveOrNull(Integer value, String fieldName) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("billing.subscription-plan.error.%s.invalid".formatted(fieldName));
        }
        return value;
    }
}
