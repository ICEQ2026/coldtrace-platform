package com.acme.coldtrace.platform.monitoring.interfaces.acl;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Published anti-corruption facade for the monitoring bounded context.
 * <p>
 * Reporting consumes immutable reading snapshots instead of importing
 * monitoring repositories or aggregates directly.
 *
 * @since 1.0
 */
public interface MonitoringContextFacade {
    /**
     * Fetches sensor reading snapshots for an organization.
     *
     * @param organizationId organization identifier
     * @return readings owned by the organization
     */
    List<SensorReadingSnapshot> fetchSensorReadingsByOrganizationId(Long organizationId);

    /**
     * Sensor reading data published to other bounded contexts.
     */
    record SensorReadingSnapshot(
            Long id,
            Long organizationId,
            Long assetId,
            Double temperature,
            Double humidity,
            Boolean outOfRange,
            OffsetDateTime recordedAt
    ) {
    }
}
