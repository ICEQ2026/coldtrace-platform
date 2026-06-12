package com.acme.coldtrace.platform.monitoring.domain.repositories;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for sensor reading aggregates.
 *
 * @since 1.0
 */
public interface SensorReadingRepository {
    /**
     * Finds all sensor readings owned by an organization.
     *
     * @param organizationId organization identifier
     * @return organization readings ordered by most recent first
     */
    List<SensorReading> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one reading by id and organization.
     *
     * @param id reading identifier
     * @param organizationId organization identifier
     * @return reading when found
     */
    Optional<SensorReading> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Persists a sensor reading aggregate.
     *
     * @param sensorReading reading aggregate
     * @return persisted reading rebuilt from persistence state
     */
    SensorReading save(SensorReading sensorReading);
}
