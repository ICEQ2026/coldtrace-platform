package com.acme.coldtrace.platform.maintenancemanagement.interfaces.events;

import java.time.OffsetDateTime;

/**
 * Integration event published by maintenance management when a service request is created.
 *
 * @param technicalServiceRequestId technical service request identifier
 * @param organizationId owning organization identifier
 * @param assetId serviced asset identifier
 * @param code public technical service request code
 * @param requestedAt request creation timestamp
 * @since 1.0
 */
public record TechnicalServiceRequestCreatedIntegrationEvent(
        Long technicalServiceRequestId,
        Long organizationId,
        Long assetId,
        String code,
        OffsetDateTime requestedAt
) {
}
