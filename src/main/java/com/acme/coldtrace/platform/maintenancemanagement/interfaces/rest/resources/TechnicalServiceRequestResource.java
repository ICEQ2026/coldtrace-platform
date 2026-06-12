package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * REST resource representing a corrective technical service request.
 *
 * @param id persistence identifier
 * @param organizationId organization that owns the request
 * @param uuid public technical service request code
 * @param assetId serviced asset identifier
 * @param assetLocationId asset location snapshot
 * @param assetName asset name snapshot
 * @param incidentId optional related incident identifier
 * @param issueDescription reported problem description
 * @param priority service priority
 * @param status lifecycle status
 * @param requestedBy optional requester name or email
 * @param requestedAt request creation timestamp
 * @param closedAt closure timestamp
 * @param closureSummary closure summary
 * @param evidence closure evidence
 * @param closedBy actor who closed the request
 * @since 1.0
 */
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
