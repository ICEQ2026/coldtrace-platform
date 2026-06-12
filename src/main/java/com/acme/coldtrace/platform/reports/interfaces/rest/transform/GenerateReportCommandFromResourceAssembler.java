package com.acme.coldtrace.platform.reports.interfaces.rest.transform;

import com.acme.coldtrace.platform.reports.domain.model.commands.GenerateReportCommand;
import com.acme.coldtrace.platform.reports.interfaces.rest.resources.GenerateReportResource;

/**
 * Assembler that converts report generation resources into commands.
 *
 * @since 1.0
 */
public class GenerateReportCommandFromResourceAssembler {
    /**
     * Converts a generation resource and route organization into a command.
     *
     * @param resource HTTP request body
     * @param organizationId organization identifier from the route
     * @return command consumed by the application layer
     */
    public static GenerateReportCommand toCommandFromResource(
            GenerateReportResource resource,
            Long organizationId
    ) {
        return new GenerateReportCommand(
                organizationId,
                resource.type(),
                resource.title(),
                resource.periodStart(),
                resource.periodEnd()
        );
    }
}
