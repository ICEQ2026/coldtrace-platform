package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateMaintenanceScheduleStatusCommand;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.UpdateMaintenanceScheduleStatusResource;

/**
 * Assembler that converts schedule status request resources into commands.
 *
 * @since 1.0
 */
public class UpdateMaintenanceScheduleStatusCommandFromResourceAssembler {
    /**
     * Converts a REST request resource into a domain command.
     *
     * @param resource request body resource
     * @param organizationId organization identifier from the route
     * @param maintenanceScheduleId maintenance schedule identifier from the route
     * @return maintenance schedule status update command
     */
    public static UpdateMaintenanceScheduleStatusCommand toCommandFromResource(
            UpdateMaintenanceScheduleStatusResource resource,
            Long organizationId,
            Long maintenanceScheduleId
    ) {
        return new UpdateMaintenanceScheduleStatusCommand(
                organizationId,
                maintenanceScheduleId,
                resource.status()
        );
    }
}
