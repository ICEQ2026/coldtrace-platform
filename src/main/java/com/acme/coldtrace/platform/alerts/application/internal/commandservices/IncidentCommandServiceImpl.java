package com.acme.coldtrace.platform.alerts.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandFailure;
import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandService;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.alerts.domain.repositories.NotificationRepository;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for incident command operations.
 * <p>
 * Incident commands are organization-scoped. Creation validates that the
 * organization exists and, when the request references an asset, that the asset
 * also belongs to the same organization. This keeps incident records aligned
 * with the asset management bounded context and prevents alerts from being
 * linked to assets outside the selected organization boundary.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class IncidentCommandServiceImpl implements IncidentCommandService {
    private final IncidentRepository incidentRepository;
    private final NotificationRepository notificationRepository;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final IdentityAccessContextFacade identityAccessContextFacade;

    public IncidentCommandServiceImpl(
            IncidentRepository incidentRepository,
            NotificationRepository notificationRepository,
            AssetManagementContextFacade assetManagementContextFacade,
            IdentityAccessContextFacade identityAccessContextFacade
    ) {
        this.incidentRepository = incidentRepository;
        this.notificationRepository = notificationRepository;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.identityAccessContextFacade = identityAccessContextFacade;
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
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for incident creation: organizationId={}", command.organizationId());
            return Result.failure(new IncidentCommandFailure.OrganizationNotFound());
        }
        if (command.assetId() != null &&
                assetManagementContextFacade.fetchAssetByIdAndOrganizationId(
                        command.organizationId(),
                        command.assetId()
                ).isEmpty()) {
            log.warn("Asset not found for incident creation: organizationId={}, assetId={}",
                    command.organizationId(), command.assetId());
            return Result.failure(new IncidentCommandFailure.AssetNotFound());
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
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
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
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
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
