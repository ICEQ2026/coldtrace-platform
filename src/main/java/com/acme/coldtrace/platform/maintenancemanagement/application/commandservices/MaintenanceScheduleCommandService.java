package com.acme.coldtrace.platform.maintenancemanagement.application.commandservices;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateMaintenanceScheduleCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateMaintenanceScheduleStatusCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application command service contract for preventive maintenance schedules.
 *
 * @since 1.0
 */
public interface MaintenanceScheduleCommandService {
    /**
     * Handles preventive maintenance schedule creation.
     *
     * @param command schedule creation command
     * @return success with persisted schedule or failure with command error
     */
    Result<MaintenanceSchedule, MaintenanceScheduleCommandFailure> handle(CreateMaintenanceScheduleCommand command);

    /**
     * Handles maintenance schedule lifecycle status updates.
     *
     * @param command status update command
     * @return success with updated schedule or failure with command error
     */
    Result<MaintenanceSchedule, MaintenanceScheduleCommandFailure> handle(UpdateMaintenanceScheduleStatusCommand command);
}
