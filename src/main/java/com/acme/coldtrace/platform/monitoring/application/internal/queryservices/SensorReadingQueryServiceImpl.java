package com.acme.coldtrace.platform.monitoring.application.internal.queryservices;

import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.monitoring.application.queryservices.SensorReadingQueryFailure;
import com.acme.coldtrace.platform.monitoring.application.queryservices.SensorReadingQueryService;
import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.model.queries.GetSensorReadingByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.monitoring.domain.model.queries.GetSensorReadingsByOrganizationIdQuery;
import com.acme.coldtrace.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for sensor reading query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SensorReadingQueryServiceImpl implements SensorReadingQueryService {
    private final SensorReadingRepository sensorReadingRepository;
    private final IamContextFacade iamContextFacade;

    public SensorReadingQueryServiceImpl(
            SensorReadingRepository sensorReadingRepository,
            IamContextFacade iamContextFacade
    ) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Retrieves readings by organization and applies optional filters.
     *
     * @param query query containing organization and optional filters
     * @return success with readings or failure with query error
     * @see GetSensorReadingsByOrganizationIdQuery
     */
    @Override
    public Result<List<SensorReading>, SensorReadingQueryFailure> handle(GetSensorReadingsByOrganizationIdQuery query) {
        if (!iamContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new SensorReadingQueryFailure.OrganizationNotFound());
        }
        var readings = sensorReadingRepository.findAllByOrganizationId(query.organizationId()).stream()
                .filter(reading -> query.assetId() == null || query.assetId().equals(reading.getAssetId()))
                .filter(reading -> query.iotDeviceId() == null || query.iotDeviceId().equals(reading.getIotDeviceId()))
                .filter(reading -> query.from() == null || !reading.getRecordedAt().isBefore(query.from()))
                .filter(reading -> query.to() == null || !reading.getRecordedAt().isAfter(query.to()))
                .toList();
        return Result.success(readings);
    }

    /**
     * Retrieves one sensor reading by id and organization.
     *
     * @param query query containing organization and reading identifiers
     * @return success with reading or failure with query error
     * @see GetSensorReadingByIdAndOrganizationIdQuery
     */
    @Override
    public Result<SensorReading, SensorReadingQueryFailure> handle(GetSensorReadingByIdAndOrganizationIdQuery query) {
        if (!iamContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new SensorReadingQueryFailure.OrganizationNotFound());
        }
        var reading = sensorReadingRepository.findByIdAndOrganizationId(query.sensorReadingId(), query.organizationId());
        if (reading.isEmpty()) {
            return Result.failure(new SensorReadingQueryFailure.SensorReadingNotFound());
        }
        return Result.success(reading.get());
    }
}
