package com.acme.coldtrace.platform.monitoring.application.commandservices;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.model.commands.CreateSensorReadingCommand;
import com.acme.coldtrace.platform.monitoring.domain.model.commands.GenerateDemoSensorReadingsCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for sensor reading command operations.
 *
 * @since 1.0
 */
public interface SensorReadingCommandService {
    /**
     * Handles explicit sensor reading creation.
     *
     * @param command command containing raw telemetry
     * @return success with created reading or failure with command error
     */
    Result<SensorReading, SensorReadingCommandFailure> handle(CreateSensorReadingCommand command);

    /**
     * Handles backend-owned demo reading generation.
     *
     * @param command command containing generation scope and count
     * @return success with persisted readings or failure with command error
     */
    Result<List<SensorReading>, SensorReadingCommandFailure> handle(GenerateDemoSensorReadingsCommand command);
}
