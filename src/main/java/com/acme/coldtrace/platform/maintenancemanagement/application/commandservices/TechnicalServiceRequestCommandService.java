package com.acme.coldtrace.platform.maintenancemanagement.application.commandservices;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application command service contract for technical service requests.
 *
 * @since 1.0
 */
public interface TechnicalServiceRequestCommandService {
    /**
     * Handles technical service request creation.
     *
     * @param command creation command
     * @return success with persisted request or failure with command error
     */
    Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(
            CreateTechnicalServiceRequestCommand command
    );

    /**
     * Handles technical service request lifecycle status updates.
     *
     * @param command status update command
     * @return success with updated request or failure with command error
     */
    Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(
            UpdateTechnicalServiceRequestStatusCommand command
    );
}
