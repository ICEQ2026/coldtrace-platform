package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.domain.repositories.NotificationRepository;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.assemblers.NotificationPersistenceAssembler;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.repositories.NotificationPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-backed adapter for the notification domain repository.
 *
 * @since 1.0
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    private final NotificationPersistenceRepository notificationPersistenceRepository;

    public NotificationRepositoryImpl(NotificationPersistenceRepository notificationPersistenceRepository) {
        this.notificationPersistenceRepository = notificationPersistenceRepository;
    }

    @Override
    public List<Notification> findAllByOrganizationId(Long organizationId) {
        return notificationPersistenceRepository.findAllByOrganizationId(organizationId).stream()
                .map(NotificationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Notification> findAllByIncidentIdAndOrganizationId(Long incidentId, Long organizationId) {
        return notificationPersistenceRepository.findAllByIncidentIdAndOrganizationId(incidentId, organizationId)
                .stream()
                .map(NotificationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            var entity = NotificationPersistenceAssembler.toPersistenceFromDomain(notification);
            return NotificationPersistenceAssembler.toDomainFromPersistence(
                    notificationPersistenceRepository.save(entity)
            );
        }

        var entity = notificationPersistenceRepository.findById(notification.getId())
                .orElseGet(() -> NotificationPersistenceAssembler.toPersistenceFromDomain(notification));
        NotificationPersistenceAssembler.copyDomainState(notification, entity);
        return NotificationPersistenceAssembler.toDomainFromPersistence(
                notificationPersistenceRepository.save(entity)
        );
    }
}
