package com.acme.coldtrace.platform.alerts.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandFailure;
import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandService;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.EscalateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.RegisterIncidentCorrectiveActionCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.alerts.domain.repositories.NotificationRepository;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.IoTDeviceSnapshot;
import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade.SensorReadingSnapshot;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service implementation for incident command operations.
 * <p>
 * Incident commands are organization-scoped. Creation validates that referenced
 * assets, IoT devices and sensor readings belong to the selected organization
 * and describe the same operational context. This keeps incident records aligned
 * with the asset management and monitoring bounded contexts.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class IncidentCommandServiceImpl implements IncidentCommandService {
    private final IncidentRepository incidentRepository;
    private final NotificationRepository notificationRepository;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final MonitoringContextFacade monitoringContextFacade;
    private final IdentityAccessContextFacade identityAccessContextFacade;

    public IncidentCommandServiceImpl(
            IncidentRepository incidentRepository,
            NotificationRepository notificationRepository,
            AssetManagementContextFacade assetManagementContextFacade,
            MonitoringContextFacade monitoringContextFacade,
            IdentityAccessContextFacade identityAccessContextFacade
    ) {
        this.incidentRepository = incidentRepository;
        this.notificationRepository = notificationRepository;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.monitoringContextFacade = monitoringContextFacade;
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
        var referenceFailure = validateIncidentReferences(command);
        if (referenceFailure.isPresent()) {
            return Result.failure(referenceFailure.get());
        }

        var incident = incidentRepository.save(new Incident(command));
        emitNotification(incident, Notification.incidentOpened(incident));

        log.info("Incident created: id={}, organizationId={}, severity={}, status={}",
                incident.getId(), incident.getOrganizationId(), incident.getSeverity(), incident.getStatus());
        return Result.success(incident);
    }

    private Optional<IncidentCommandFailure> validateIncidentReferences(CreateIncidentCommand command) {
        if (command.assetId() != null &&
                assetManagementContextFacade.fetchAssetByIdAndOrganizationId(
                        command.organizationId(),
                        command.assetId()
                ).isEmpty()) {
            log.warn("Asset not found for incident creation: organizationId={}, assetId={}",
                    command.organizationId(), command.assetId());
            return Optional.of(new IncidentCommandFailure.AssetNotFound());
        }

        var device = Optional.<IoTDeviceSnapshot>empty();
        if (command.deviceId() != null) {
            device = assetManagementContextFacade.fetchIoTDeviceByIdAndOrganizationId(
                    command.organizationId(),
                    command.deviceId()
            );
            if (device.isEmpty()) {
                log.warn("IoT device not found for incident creation: organizationId={}, deviceId={}",
                        command.organizationId(), command.deviceId());
                return Optional.of(new IncidentCommandFailure.DeviceNotFound());
            }
            if (command.assetId() != null && !command.assetId().equals(device.get().assetId())) {
                log.warn("Incident asset/device mismatch: organizationId={}, assetId={}, deviceId={}",
                        command.organizationId(), command.assetId(), command.deviceId());
                return Optional.of(new IncidentCommandFailure.InconsistentReference());
            }
        }

        if (command.readingId() == null) {
            return Optional.empty();
        }

        var reading = monitoringContextFacade.fetchSensorReadingByIdAndOrganizationId(
                command.organizationId(),
                command.readingId()
        );
        if (reading.isEmpty()) {
            log.warn("Sensor reading not found for incident creation: organizationId={}, readingId={}",
                    command.organizationId(), command.readingId());
            return Optional.of(new IncidentCommandFailure.ReadingNotFound());
        }
        return validateReadingReference(command, reading.get());
    }

    private Optional<IncidentCommandFailure> validateReadingReference(
            CreateIncidentCommand command,
            SensorReadingSnapshot reading
    ) {
        if (command.assetId() != null && !command.assetId().equals(reading.assetId())) {
            log.warn("Incident asset/reading mismatch: organizationId={}, assetId={}, readingId={}",
                    command.organizationId(), command.assetId(), command.readingId());
            return Optional.of(new IncidentCommandFailure.InconsistentReference());
        }
        if (command.deviceId() != null && !command.deviceId().equals(reading.iotDeviceId())) {
            log.warn("Incident device/reading mismatch: organizationId={}, deviceId={}, readingId={}",
                    command.organizationId(), command.deviceId(), command.readingId());
            return Optional.of(new IncidentCommandFailure.InconsistentReference());
        }
        return Optional.empty();
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
     * Handles escalation of an active incident aggregate.
     *
     * @param command escalation command
     * @return success with escalated incident or failure with command error
     */
    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(EscalateIncidentCommand command) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for incident escalation: organizationId={}", command.organizationId());
            return Result.failure(new IncidentCommandFailure.OrganizationNotFound());
        }

        var incident = incidentRepository.findByIdAndOrganizationId(command.incidentId(), command.organizationId());
        if (incident.isEmpty()) {
            log.warn("Incident not found for escalation: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.IncidentNotFound());
        }
        if (incident.get().isResolved()) {
            log.warn("Resolved incident cannot be escalated: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.AlreadyResolved());
        }
        if (incident.get().isEscalated()) {
            log.warn("Incident already escalated: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.AlreadyEscalated());
        }

        incident.get().escalate(command);
        var escalatedIncident = incidentRepository.save(incident.get());

        log.info("Incident escalated: id={}, organizationId={}",
                escalatedIncident.getId(), escalatedIncident.getOrganizationId());
        return Result.success(escalatedIncident);
    }

    /**
     * Handles corrective action registration for an active incident aggregate.
     *
     * @param command corrective action command
     * @return success with updated incident or failure with command error
     */
    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(RegisterIncidentCorrectiveActionCommand command) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for incident corrective action: organizationId={}", command.organizationId());
            return Result.failure(new IncidentCommandFailure.OrganizationNotFound());
        }

        var incident = incidentRepository.findByIdAndOrganizationId(command.incidentId(), command.organizationId());
        if (incident.isEmpty()) {
            log.warn("Incident not found for corrective action: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.IncidentNotFound());
        }
        if (incident.get().isResolved()) {
            log.warn("Resolved incident cannot receive corrective action: organizationId={}, incidentId={}",
                    command.organizationId(), command.incidentId());
            return Result.failure(new IncidentCommandFailure.AlreadyResolved());
        }

        incident.get().registerCorrectiveAction(command);
        var updatedIncident = incidentRepository.save(incident.get());

        log.info("Incident corrective action registered: id={}, organizationId={}",
                updatedIncident.getId(), updatedIncident.getOrganizationId());
        return Result.success(updatedIncident);
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
