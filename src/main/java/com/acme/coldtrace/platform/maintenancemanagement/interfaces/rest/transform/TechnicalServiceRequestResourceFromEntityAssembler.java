package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.TechnicalServiceRequestResource;

public final class TechnicalServiceRequestResourceFromEntityAssembler {
    private TechnicalServiceRequestResourceFromEntityAssembler() {
    }

    public static TechnicalServiceRequestResource toResourceFromEntity(TechnicalServiceRequest request) {
        return new TechnicalServiceRequestResource(request.getId(), request.getOrganizationId(), request.getCode(),
                request.getAssetId(), request.getAssetLocationId(), request.getAssetName(), request.getIncidentId(),
                request.getIssueDescription(), request.getPriority(), request.getStatus(), request.getRequestedBy(),
                request.getRequestedAt(), request.getClosedAt(), request.getClosureSummary(), request.getEvidence(),
                request.getClosedBy());
    }
}
