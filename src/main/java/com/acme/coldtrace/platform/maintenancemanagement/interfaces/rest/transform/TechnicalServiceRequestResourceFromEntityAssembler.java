package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.TechnicalServiceRequestResource;

/**
 * Assembler that converts technical service request aggregates into REST resources.
 *
 * @since 1.0
 */
public final class TechnicalServiceRequestResourceFromEntityAssembler {
    private TechnicalServiceRequestResourceFromEntityAssembler() {
    }

    /**
     * Converts a technical service request aggregate into a REST resource.
     *
     * @param request technical service request aggregate
     * @return technical service request resource
     */
    public static TechnicalServiceRequestResource toResourceFromEntity(TechnicalServiceRequest request) {
        return new TechnicalServiceRequestResource(
                request.getId(),
                request.getOrganizationId(),
                request.getCode(),
                request.getAssetId(),
                request.getAssetLocationId(),
                request.getAssetName(),
                request.getIncidentId(),
                request.getIssueDescription(),
                request.getPriority(),
                request.getStatus(),
                request.getRequestedBy(),
                request.getRequestedAt(),
                request.getClosedAt(),
                request.getClosureSummary(),
                request.getEvidence(),
                request.getClosedBy()
        );
    }
}
