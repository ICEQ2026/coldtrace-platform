package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for querying and persisting incident notification read models.
 *
 * @since 1.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Finds all notifications owned by an organization.
     *
     * @param organizationId organization identifier
     * @return notification read models
     */
    List<Notification> findAllByOrganizationId(Long organizationId);

    /**
     * Finds notifications for one incident scoped by organization.
     *
     * @param incidentId incident identifier
     * @param organizationId organization identifier
     * @return notification read models
     */
    List<Notification> findAllByIncidentIdAndOrganizationId(Long incidentId, Long organizationId);
}
