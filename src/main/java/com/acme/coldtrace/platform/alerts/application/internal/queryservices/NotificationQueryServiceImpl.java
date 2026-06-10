package com.acme.coldtrace.platform.alerts.application.internal.queryservices;

import com.acme.coldtrace.platform.alerts.application.queryservices.NotificationQueryFailure;
import com.acme.coldtrace.platform.alerts.application.queryservices.NotificationQueryService;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetNotificationsByIncidentIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.alerts.domain.model.queries.GetNotificationsByOrganizationIdQuery;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.alerts.domain.repositories.NotificationRepository;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for notification query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationRepository notificationRepository;
    private final IncidentRepository incidentRepository;
    private final OrganizationRepository organizationRepository;

    public NotificationQueryServiceImpl(
            NotificationRepository notificationRepository,
            IncidentRepository incidentRepository,
            OrganizationRepository organizationRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.incidentRepository = incidentRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Retrieves notification read models by organization.
     *
     * @param query query object containing organization identifier
     * @return success with notification list or failure with query error
     */
    @Override
    public Result<List<Notification>, NotificationQueryFailure> handle(GetNotificationsByOrganizationIdQuery query) {
        if (!organizationRepository.existsById(query.organizationId())) {
            log.warn("Organization not found for notification query: organizationId={}", query.organizationId());
            return Result.failure(new NotificationQueryFailure.OrganizationNotFound());
        }
        var notifications = notificationRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} notifications for organizationId={}", notifications.size(), query.organizationId());
        return Result.success(notifications);
    }

    /**
     * Retrieves notification read models by incident and organization.
     *
     * @param query query object containing organization and incident identifiers
     * @return success with notification list or failure with query error
     */
    @Override
    public Result<List<Notification>, NotificationQueryFailure> handle(GetNotificationsByIncidentIdAndOrganizationIdQuery query) {
        if (!organizationRepository.existsById(query.organizationId())) {
            log.warn("Organization not found for incident notification query: organizationId={}", query.organizationId());
            return Result.failure(new NotificationQueryFailure.OrganizationNotFound());
        }
        if (!incidentRepository.existsByIdAndOrganizationId(query.incidentId(), query.organizationId())) {
            log.warn("Incident not found for notification query: organizationId={}, incidentId={}",
                    query.organizationId(), query.incidentId());
            return Result.failure(new NotificationQueryFailure.IncidentNotFound());
        }
        var notifications = notificationRepository.findAllByIncidentIdAndOrganizationId(
                query.incidentId(),
                query.organizationId()
        );
        log.debug("Found {} notifications for organizationId={}, incidentId={}",
                notifications.size(), query.organizationId(), query.incidentId());
        return Result.success(notifications);
    }
}
