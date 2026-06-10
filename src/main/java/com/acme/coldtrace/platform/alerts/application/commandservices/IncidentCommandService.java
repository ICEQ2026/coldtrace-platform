package com.acme.coldtrace.platform.alerts.application.commandservices;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for incident command operations.
 *
 * @since 1.0
 */
public interface IncidentCommandService {
    /**
     * Handles incident creation.
     *
     * @param command command containing incident data
     * @return success with created incident or failure with command error
     */
    Result<Incident, IncidentCommandFailure> handle(CreateIncidentCommand command);

    /**
     * Handles incident acknowledgement.
     *
     * @param command acknowledgement command
     * @return success with acknowledged incident or failure with command error
     */
    Result<Incident, IncidentCommandFailure> handle(AcknowledgeIncidentCommand command);

    /**
     * Handles incident resolution.
     *
     * @param command resolution command
     * @return success with resolved incident or failure with command error
     */
    Result<Incident, IncidentCommandFailure> handle(ResolveIncidentCommand command);
}
