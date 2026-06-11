package com.acme.coldtrace.platform.maintenancemanagement.domain.model.events;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;

import java.time.OffsetDateTime;

/**
 * Domain event raised when a preventive maintenance schedule is created.
 *
 * @param maintenanceScheduleId maintenance schedule identifier
 * @param organizationId owning organization identifier
 * @param assetId maintained asset identifier
 * @param uuid public maintenance schedule code
 * @param scheduledDate planned maintenance date
 * @since 1.0
 */
public record MaintenanceScheduleCreatedEvent(
        Long maintenanceScheduleId,
        Long organizationId,
        Long assetId,
        String uuid,
        OffsetDateTime scheduledDate
) {
    /**
     * Builds the event from a persisted maintenance schedule aggregate.
     *
     * @param schedule source aggregate
     * @return maintenance-schedule-created event
     */
    public static MaintenanceScheduleCreatedEvent from(MaintenanceSchedule schedule) {
        return new MaintenanceScheduleCreatedEvent(
                schedule.getId(),
                schedule.getOrganizationId(),
                schedule.getAssetId(),
                schedule.getUuid(),
                schedule.getScheduledDate()
        );
    }
}
