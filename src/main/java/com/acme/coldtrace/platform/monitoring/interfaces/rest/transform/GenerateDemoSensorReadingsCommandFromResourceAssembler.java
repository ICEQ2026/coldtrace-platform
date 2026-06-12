package com.acme.coldtrace.platform.monitoring.interfaces.rest.transform;

import com.acme.coldtrace.platform.monitoring.domain.model.commands.GenerateDemoSensorReadingsCommand;
import com.acme.coldtrace.platform.monitoring.interfaces.rest.resources.GenerateDemoSensorReadingsResource;

/**
 * Assembler that converts demo generation resources into commands.
 *
 * @since 1.0
 */
public class GenerateDemoSensorReadingsCommandFromResourceAssembler {
    /**
     * Converts a generation resource and route organization into a command.
     *
     * @param resource HTTP request body
     * @param organizationId organization identifier from the route
     * @return command consumed by the application layer
     */
    public static GenerateDemoSensorReadingsCommand toCommandFromResource(
            GenerateDemoSensorReadingsResource resource,
            Long organizationId
    ) {
        return new GenerateDemoSensorReadingsCommand(
                organizationId,
                resource == null ? null : resource.assetId(),
                resource == null ? null : resource.count()
        );
    }
}
