package com.acme.coldtrace.platform.maintenancemanagement.interfaces.events;

import java.time.OffsetDateTime;

/**
 * Integration event published by maintenance management when a schedule is created.
 *
 * @param maintenanceScheduleId maintenance schedule identifier
 * @param organizationId owning organization identifier
 * @param assetId maintained asset identifier
 * @param uuid public maintenance schedule code
 * @param scheduledDate planned maintenance date
 * @since 1.0
 */
public record MaintenanceScheduleCreatedIntegrationEvent(
        Long maintenanceScheduleId,
        Long organizationId,
        Long assetId,
        String uuid,
        OffsetDateTime scheduledDate
) {
}
