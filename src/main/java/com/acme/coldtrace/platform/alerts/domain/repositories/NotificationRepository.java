package com.acme.coldtrace.platform.alerts.domain.repositories;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;

import java.util.List;

/**
 * Domain repository contract for notification read models.
 *
 * @since 1.0
 */
public interface NotificationRepository {
    List<Notification> findAllByOrganizationId(Long organizationId);

    List<Notification> findAllByIncidentIdAndOrganizationId(Long incidentId, Long organizationId);

    Notification save(Notification notification);
}
