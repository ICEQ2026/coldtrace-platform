package com.acme.coldtrace.platform.monitoring.domain.model.queries;

/**
 * Query for retrieving one sensor reading inside an organization.
 *
 * @param organizationId organization identifier
 * @param sensorReadingId sensor reading identifier
 * @since 1.0
 */
public record GetSensorReadingByIdAndOrganizationIdQuery(Long organizationId, Long sensorReadingId) {
    /**
     * Validates query identifiers.
     *
     * @throws IllegalArgumentException when identifiers are missing or invalid
     */
    public GetSensorReadingByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.organizationId.invalid");
        }
        if (sensorReadingId == null || sensorReadingId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.sensorReadingId.invalid");
        }
    }
}
