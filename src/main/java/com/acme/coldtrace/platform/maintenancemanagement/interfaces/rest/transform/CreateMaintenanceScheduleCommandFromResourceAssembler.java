package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateMaintenanceScheduleCommand;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.CreateMaintenanceScheduleResource;

/**
 * Assembler that converts schedule creation request resources into commands.
 *
 * @since 1.0
 */
public class CreateMaintenanceScheduleCommandFromResourceAssembler {
    /**
     * Converts a REST request resource into a domain command.
     *
     * @param resource request body resource
     * @param organizationId organization identifier from the route
     * @return maintenance schedule creation command
     */
    public static CreateMaintenanceScheduleCommand toCommandFromResource(
            CreateMaintenanceScheduleResource resource,
            Long organizationId
    ) {
        return new CreateMaintenanceScheduleCommand(
                organizationId,
                resource.assetId(),
                resource.scheduledDate(),
                resource.frequencyDays(),
                resource.responsibleUserId(),
                resource.observations(),
                resource.status()
        );
    }
}
