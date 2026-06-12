package com.acme.coldtrace.platform.monitoring.domain.model.commands;

/**
 * Command for generating backend-owned demo sensor readings.
 * <p>
 * Demo generation replaces the temporary frontend randomization flow. The
 * backend selects eligible assigned devices, produces values compatible with
 * their measurement parameters, evaluates them against asset settings and
 * persists the resulting readings.
 *
 * @param organizationId organization identifier from the route
 * @param assetId optional asset filter
 * @param count number of readings to generate
 * @since 1.0
 */
public record GenerateDemoSensorReadingsCommand(Long organizationId, Long assetId, Integer count) {
    /**
     * Validates and normalizes demo generation parameters.
     *
     * @throws IllegalArgumentException when identifiers or count are invalid
     */
    public GenerateDemoSensorReadingsCommand {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.organizationId.invalid");
        }
        if (assetId != null && assetId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.assetId.invalid");
        }
        count = count == null ? 1 : count;
        if (count <= 0 || count > 50) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.demo-count.invalid");
        }
    }
}
