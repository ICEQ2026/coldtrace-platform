package com.acme.coldtrace.platform.monitoring.domain.model.queries;

import java.time.OffsetDateTime;

/**
 * Query for retrieving sensor readings by organization and optional filters.
 *
 * @param organizationId organization identifier
 * @param assetId optional asset filter
 * @param iotDeviceId optional IoT device filter
 * @param from optional inclusive lower timestamp bound
 * @param to optional inclusive upper timestamp bound
 * @since 1.0
 */
public record GetSensorReadingsByOrganizationIdQuery(
        Long organizationId,
        Long assetId,
        Long iotDeviceId,
        OffsetDateTime from,
        OffsetDateTime to
) {
    /**
     * Validates query identifiers and date range.
     *
     * @throws IllegalArgumentException when identifiers or date range are invalid
     */
    public GetSensorReadingsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.organizationId.invalid");
        }
        if (assetId != null && assetId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.assetId.invalid");
        }
        if (iotDeviceId != null && iotDeviceId <= 0) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.iotDeviceId.invalid");
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("monitoring.sensor-reading.error.date-range.invalid");
        }
    }
}
