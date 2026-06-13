package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import java.time.OffsetDateTime;

/**
 * REST resource representing a preventive maintenance schedule.
 *
 * @param id persistence identifier
 * @param organizationId organization that owns the schedule
 * @param uuid public maintenance schedule code
 * @param assetId maintained asset identifier
 * @param scheduledDate planned maintenance date and time
 * @param frequencyDays optional recurrence cadence in days
 * @param responsibleUserId optional responsible organization user identifier
 * @param observations optional planning observations
 * @param status lifecycle status
 * @param createdAt domain creation timestamp exposed to API clients
 * @since 1.0
 */
public record MaintenanceScheduleResource(
        Long id,
        Long organizationId,
        String uuid,
        Long assetId,
        OffsetDateTime scheduledDate,
        Integer frequencyDays,
        Long responsibleUserId,
        String observations,
        String status,
        OffsetDateTime createdAt
) {
}
