package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;

/**
 * Assembler that translates notifications between domain and persistence models.
 *
 * @since 1.0
 */
public final class NotificationPersistenceAssembler {
    private NotificationPersistenceAssembler() {
    }

    public static Notification toDomainFromPersistence(NotificationPersistenceEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getIncidentId(),
                entity.getChannel(),
                entity.getRecipient(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getDeliveredAt(),
                entity.getFailureReason()
        );
    }

    public static NotificationPersistenceEntity toPersistenceFromDomain(Notification notification) {
        var entity = new NotificationPersistenceEntity();
        entity.setId(notification.getId());
        copyDomainState(notification, entity);
        return entity;
    }

    public static void copyDomainState(Notification notification, NotificationPersistenceEntity entity) {
        entity.setOrganizationId(notification.getOrganizationId());
        entity.setIncidentId(notification.getIncidentId());
        entity.setChannel(notification.getChannel());
        entity.setRecipient(notification.getRecipient());
        entity.setMessage(notification.getMessage());
        entity.setStatus(notification.getStatus());
        entity.setDeliveredAt(notification.getDeliveredAt());
        entity.setFailureReason(notification.getFailureReason());
    }
}
