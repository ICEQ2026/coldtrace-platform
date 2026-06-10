package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.CreateIncidentResource;

/**
 * Interface layer translator converting create incident resources to commands.
 *
 * @since 1.0
 */
public class CreateIncidentCommandFromResourceAssembler {
    /**
     * Converts a create incident request into a command.
     *
     * @param resource create incident request resource
     * @param organizationId organization identifier from the route
     * @return create incident command
     */
    public static CreateIncidentCommand toCommandFromResource(
            CreateIncidentResource resource,
            Long organizationId
    ) {
        return new CreateIncidentCommand(
                organizationId,
                resource.assetId(),
                resource.deviceId(),
                resource.readingId(),
                resource.assetName(),
                resource.deviceName(),
                resource.type(),
                resource.severity(),
                resource.value()
        );
    }
}
