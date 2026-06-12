package com.acme.coldtrace.platform.maintenancemanagement.domain.model.events;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;

import java.time.OffsetDateTime;

/**
 * Domain event raised when a technical service request is created.
 *
 * @param technicalServiceRequestId technical service request identifier
 * @param organizationId owning organization identifier
 * @param assetId serviced asset identifier
 * @param code public technical service request code
 * @param requestedAt request creation timestamp
 * @since 1.0
 */
public record TechnicalServiceRequestCreatedEvent(
        Long technicalServiceRequestId,
        Long organizationId,
        Long assetId,
        String code,
        OffsetDateTime requestedAt
) {
    /**
     * Builds the event from a persisted technical service request aggregate.
     *
     * @param request source aggregate
     * @return technical-service-request-created event
     */
    public static TechnicalServiceRequestCreatedEvent from(TechnicalServiceRequest request) {
        return new TechnicalServiceRequestCreatedEvent(
                request.getId(),
                request.getOrganizationId(),
                request.getAssetId(),
                request.getCode(),
                request.getRequestedAt()
        );
    }
}
