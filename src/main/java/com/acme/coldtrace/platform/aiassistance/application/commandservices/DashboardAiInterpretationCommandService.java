package com.acme.coldtrace.platform.aiassistance.application.commandservices;

import com.acme.coldtrace.platform.aiassistance.application.model.DashboardAiInterpretation;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateDashboardAiInterpretationCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application command service for dashboard AI interpretation use cases.
 *
 * @since 1.0
 */
public interface DashboardAiInterpretationCommandService {
    /**
     * Generates a structured advisory interpretation from persisted dashboard data.
     *
     * @param command command containing organization and optional question
     * @return success with generated interpretation or failure with controlled reason
     */
    Result<DashboardAiInterpretation, DashboardAiInterpretationCommandFailure> handle(
            GenerateDashboardAiInterpretationCommand command);
}
