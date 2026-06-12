package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.UpdateTechnicalServiceRequestStatusResource;

public final class UpdateTechnicalServiceRequestStatusCommandFromResourceAssembler {
    private UpdateTechnicalServiceRequestStatusCommandFromResourceAssembler() {
    }

    public static UpdateTechnicalServiceRequestStatusCommand toCommandFromResource(
            UpdateTechnicalServiceRequestStatusResource resource, Long organizationId, Long technicalServiceRequestId) {
        return new UpdateTechnicalServiceRequestStatusCommand(organizationId, technicalServiceRequestId, resource.status(),
                resource.closureSummary(), resource.evidence(), resource.closedBy());
    }
}
