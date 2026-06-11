package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities.MaintenanceSchedulePersistenceEntity;

/**
 * Assembler that translates maintenance schedules between domain and persistence models.
 *
 * @since 1.0
 */
public final class MaintenanceSchedulePersistenceAssembler {
    private MaintenanceSchedulePersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return maintenance schedule aggregate rebuilt from persisted state
     */
    public static MaintenanceSchedule toDomainFromPersistence(MaintenanceSchedulePersistenceEntity entity) {
        return new MaintenanceSchedule(
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

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param schedule maintenance schedule aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static MaintenanceSchedulePersistenceEntity toPersistenceFromDomain(MaintenanceSchedule schedule) {
        var entity = new MaintenanceSchedulePersistenceEntity();
        copyDomainState(schedule, entity);
        return entity;
    }

    /**
     * Copies mutable domain state into an existing managed JPA entity.
     *
     * @param schedule source aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(
            MaintenanceSchedule schedule,
            MaintenanceSchedulePersistenceEntity entity
    ) {
        entity.setId(schedule.getId());
        entity.setOrganizationId(schedule.getOrganizationId());
        entity.setUuid(schedule.getUuid());
        entity.setAssetId(schedule.getAssetId());
        entity.setScheduledDate(schedule.getScheduledDate());
        entity.setFrequencyDays(schedule.getFrequencyDays());
        entity.setResponsibleUserId(schedule.getResponsibleUserId());
        entity.setObservations(schedule.getObservations());
        entity.setStatus(schedule.getStatus());
        entity.setRegisteredAt(schedule.getRegisteredAt());
    }
}
