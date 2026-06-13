package com.acme.coldtrace.platform.alerts.application.queryservices;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetNotificationsByIncidentIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetNotificationsByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract providing read access to notification read models.
 *
 * @since 1.0
 */
public interface NotificationQueryService {
    /**
     * Retrieves notifications by organization.
     *
     * @param query query object containing organization identifier
     * @return success with notification list or failure with query error
     */
    Result<List<Notification>, NotificationQueryFailure> handle(GetNotificationsByOrganizationIdQuery query);

    /**
     * Retrieves notifications for one incident scoped by organization.
     *
     * @param query query object containing organization and incident identifiers
     * @return success with notification list or failure with query error
     */
    Result<List<Notification>, NotificationQueryFailure> handle(GetNotificationsByIncidentIdAndOrganizationIdQuery query);
}
