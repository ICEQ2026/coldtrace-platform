package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.CreateTechnicalServiceRequestResource;

public final class CreateTechnicalServiceRequestCommandFromResourceAssembler {
    private CreateTechnicalServiceRequestCommandFromResourceAssembler() {
    }

    public static CreateTechnicalServiceRequestCommand toCommandFromResource(CreateTechnicalServiceRequestResource resource, Long organizationId) {
        return new CreateTechnicalServiceRequestCommand(organizationId, resource.assetId(), resource.incidentId(),
                resource.issueDescription(), resource.priority(), resource.requestedBy());
    }
}
