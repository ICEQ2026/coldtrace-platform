package com.acme.coldtrace.platform.alerts.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandFailure;
import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandService;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.IncidentRepository;
import com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.NotificationRepository;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.OrganizationRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for incident command operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class IncidentCommandServiceImpl implements IncidentCommandService {
    private final IncidentRepository incidentRepository;
    private final NotificationRepository notificationRepository;
    private final OrganizationRepository organizationRepository;

    public IncidentCommandServiceImpl(
            IncidentRepository incidentRepository,
            NotificationRepository notificationRepository,
            OrganizationRepository organizationRepository
    ) {
        this.incidentRepository = incidentRepository;
        this.notificationRepository = notificationRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Handles creation of an incident aggregate.
     *
     * @param command command containing incident data
     * @return success with created incident or failure with command error
     */
    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(CreateIncidentCommand command) {
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for incident creation: organizationId={}", command.organizationId());
            return Result.failure(new IncidentCommandFailure.OrganizationNotFound());
        }

        var incident = incidentRepository.save(new Incident(command));
        emitNotification(incident, Notification.incidentOpened(incident));

        log.info("Incident created: id={}, organizationId={}, severity={}, status={}",
                incident.getId(), incident.getOrganizationId(), incident.getSeverity(), incident.getStatus());
        return Result.success(incident);
    }

    /**
     * Handles acknowledgement of an incident aggregate.
     *
     * @param command acknowledgement command
     * @return success with acknowledged incident or failure with command error
     */
    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(AcknowledgeIncidentCommand command) {
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for incident acknowledgement: organizationId={}", command.organizationId());
            return Result.failure(new IncidentCommandFailure.OrganizationNotFound());
        }

        var incident = incidentRepository.findByIdAndOrganizationId(command.incidentId(), command.organizationId());
        if (incident.isEmpty()) {
            log.warn("Incident not found for acknowledgement: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.IncidentNotFound());
        }
        if (incident.get().isResolved()) {
            log.warn("Resolved incident cannot be acknowledged: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.AlreadyResolved());
        }
        if (incident.get().isAcknowledged()) {
            log.warn("Incident already acknowledged: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.AlreadyAcknowledged());
        }

        incident.get().acknowledge(command);
        var acknowledgedIncident = incidentRepository.save(incident.get());
        emitNotification(acknowledgedIncident, Notification.incidentAcknowledged(acknowledgedIncident));

        log.info("Incident acknowledged: id={}, organizationId={}",
                acknowledgedIncident.getId(), acknowledgedIncident.getOrganizationId());
        return Result.success(acknowledgedIncident);
    }

    /**
     * Handles resolution of an incident aggregate.
     *
     * @param command resolution command
     * @return success with resolved incident or failure with command error
     */
    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(ResolveIncidentCommand command) {
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for incident resolution: organizationId={}", command.organizationId());
            return Result.failure(new IncidentCommandFailure.OrganizationNotFound());
        }

        var incident = incidentRepository.findByIdAndOrganizationId(command.incidentId(), command.organizationId());
        if (incident.isEmpty()) {
            log.warn("Incident not found for resolution: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.IncidentNotFound());
        }
        if (incident.get().isResolved()) {
            log.warn("Incident already resolved: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.AlreadyResolved());
        }
        if (!incident.get().isOpen() && !incident.get().isAcknowledged()) {
            log.warn("Invalid incident resolution transition: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.InvalidLifecycleTransition());
        }

        incident.get().resolve(command);
        var resolvedIncident = incidentRepository.save(incident.get());
        emitNotification(resolvedIncident, Notification.incidentResolved(resolvedIncident));

        log.info("Incident resolved: id={}, organizationId={}",
                resolvedIncident.getId(), resolvedIncident.getOrganizationId());
        return Result.success(resolvedIncident);
    }

    private void emitNotification(Incident incident, Notification notification) {
        var emittedNotification = notificationRepository.save(notification);
        incident.recordNotification(emittedNotification.getStatus());
        incidentRepository.save(incident);
    }
}
