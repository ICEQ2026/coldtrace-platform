package com.acme.coldtrace.platform.monitoring.application.queryservices;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.model.queries.GetSensorReadingByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.monitoring.domain.model.queries.GetSensorReadingsByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for sensor reading query operations.
 *
 * @since 1.0
 */
public interface SensorReadingQueryService {
    /**
     * Retrieves readings for an organization and optional filters.
     *
     * @param query query containing organization and optional filters
     * @return success with readings or failure with query error
     */
    Result<List<SensorReading>, SensorReadingQueryFailure> handle(GetSensorReadingsByOrganizationIdQuery query);

    /**
     * Retrieves one sensor reading by id and organization.
     *
     * @param query query containing organization and reading identifiers
     * @return success with reading or failure with query error
     */
    Result<SensorReading, SensorReadingQueryFailure> handle(GetSensorReadingByIdAndOrganizationIdQuery query);
}
