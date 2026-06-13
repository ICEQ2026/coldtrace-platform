package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for notification persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface NotificationPersistenceRepository extends JpaRepository<NotificationPersistenceEntity, Long> {
    List<NotificationPersistenceEntity> findAllByOrganizationId(Long organizationId);

    List<NotificationPersistenceEntity> findAllByIncidentIdAndOrganizationId(Long incidentId, Long organizationId);
}
