package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;

/**
 * Assembler that translates incidents between domain and persistence models.
 *
 * @since 1.0
 */
public final class IncidentPersistenceAssembler {
    private IncidentPersistenceAssembler() {
    }

    public static Incident toDomainFromPersistence(IncidentPersistenceEntity entity) {
        return new Incident(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getDeviceId(),
                entity.getReadingId(),
                entity.getAssetName(),
                entity.getDeviceName(),
                entity.getType(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getValue(),
                entity.getDetectedAt(),
                entity.getAcknowledgedAt(),
                entity.getAcknowledgedBy(),
                entity.getResolvedAt(),
                entity.getResolvedBy(),
                entity.getResolutionNotes(),
                entity.getLastNotificationStatus(),
                entity.getLastNotificationAt(),
                entity.getNotificationCount()
        );
    }

    public static IncidentPersistenceEntity toPersistenceFromDomain(Incident incident) {
        var entity = new IncidentPersistenceEntity();
        entity.setId(incident.getId());
        copyDomainState(incident, entity);
        return entity;
    }

    public static void copyDomainState(Incident incident, IncidentPersistenceEntity entity) {
        entity.setOrganizationId(incident.getOrganizationId());
        entity.setAssetId(incident.getAssetId());
        entity.setDeviceId(incident.getDeviceId());
        entity.setReadingId(incident.getReadingId());
        entity.setAssetName(incident.getAssetName());
        entity.setDeviceName(incident.getDeviceName());
        entity.setType(incident.getType());
        entity.setSeverity(incident.getSeverity());
        entity.setStatus(incident.getStatus());
        entity.setValue(incident.getValue());
        entity.setDetectedAt(incident.getDetectedAt());
        entity.setAcknowledgedAt(incident.getAcknowledgedAt());
        entity.setAcknowledgedBy(incident.getAcknowledgedBy());
        entity.setResolvedAt(incident.getResolvedAt());
        entity.setResolvedBy(incident.getResolvedBy());
        entity.setResolutionNotes(incident.getResolutionNotes());
        entity.setLastNotificationStatus(incident.getLastNotificationStatus());
        entity.setLastNotificationAt(incident.getLastNotificationAt());
        entity.setNotificationCount(incident.getNotificationCount());
    }
}
