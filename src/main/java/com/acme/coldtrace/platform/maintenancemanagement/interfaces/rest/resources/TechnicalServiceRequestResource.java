package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(name = "TechnicalServiceRequest", description = "Corrective technical service request resource")
public record TechnicalServiceRequestResource(
        Long id,
        Long organizationId,
        String uuid,
        Long assetId,
        Long assetLocationId,
        String assetName,
        Long incidentId,
        String issueDescription,
        String priority,
        String status,
        String requestedBy,
        OffsetDateTime requestedAt,
        OffsetDateTime closedAt,
        String closureSummary,
        String evidence,
        String closedBy
) {
}
