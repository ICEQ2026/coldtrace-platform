package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.CreateTechnicalServiceRequestResource;

/**
 * Assembler that converts technical service request creation resources into commands.
 *
 * @since 1.0
 */
public final class CreateTechnicalServiceRequestCommandFromResourceAssembler {
    private CreateTechnicalServiceRequestCommandFromResourceAssembler() {
    }

    /**
     * Converts a REST request resource into a domain command.
     *
     * @param resource request body resource
     * @param organizationId organization identifier from the route
     * @return technical service request creation command
     */
    public static CreateTechnicalServiceRequestCommand toCommandFromResource(
            CreateTechnicalServiceRequestResource resource,
            Long organizationId
    ) {
        return new CreateTechnicalServiceRequestCommand(
                organizationId,
                resource.assetId(),
                resource.incidentId(),
                resource.issueDescription(),
                resource.priority(),
                resource.requestedBy()
        );
    }
}
