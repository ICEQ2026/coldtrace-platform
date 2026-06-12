package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.UpdateTechnicalServiceRequestStatusResource;

/**
 * Assembler that converts technical service request status resources into commands.
 *
 * @since 1.0
 */
public final class UpdateTechnicalServiceRequestStatusCommandFromResourceAssembler {
    private UpdateTechnicalServiceRequestStatusCommandFromResourceAssembler() {
    }

    /**
     * Converts a REST request resource into a domain command.
     *
     * @param resource request body resource
     * @param organizationId organization identifier from the route
     * @param technicalServiceRequestId technical service request identifier from the route
     * @return technical service request status update command
     */
    public static UpdateTechnicalServiceRequestStatusCommand toCommandFromResource(
            UpdateTechnicalServiceRequestStatusResource resource,
            Long organizationId,
            Long technicalServiceRequestId
    ) {
        return new UpdateTechnicalServiceRequestStatusCommand(
                organizationId,
                technicalServiceRequestId,
                resource.status(),
                resource.closureSummary(),
                resource.evidence(),
                resource.closedBy()
        );
    }
}
