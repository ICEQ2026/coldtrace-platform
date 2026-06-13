package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources.MaintenanceScheduleResource;

/**
 * Assembler that converts maintenance schedule aggregates into REST resources.
 *
 * @since 1.0
 */
public class MaintenanceScheduleResourceFromEntityAssembler {
    /**
     * Converts a maintenance schedule aggregate into a REST resource.
     *
     * @param entity maintenance schedule aggregate
     * @return maintenance schedule resource
     */
    public static MaintenanceScheduleResource toResourceFromEntity(MaintenanceSchedule entity) {
        return new MaintenanceScheduleResource(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUuid(),
                entity.getAssetId(),
                entity.getScheduledDate(),
                entity.getFrequencyDays(),
                entity.getResponsibleUserId(),
                entity.getObservations(),
                entity.getStatus(),
                entity.getRegisteredAt()
        );
    }
}
