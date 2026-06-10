package com.acme.coldtrace.platform.reports.application.commandservices;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.model.commands.GenerateReportCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for report command operations.
 *
 * @since 1.0
 */
public interface ReportCommandService {
    /**
     * Handles report generation.
     *
     * @param command command containing report scope
     * @return success with generated report or failure with command error
     */
    Result<Report, ReportCommandFailure> handle(GenerateReportCommand command);
}
