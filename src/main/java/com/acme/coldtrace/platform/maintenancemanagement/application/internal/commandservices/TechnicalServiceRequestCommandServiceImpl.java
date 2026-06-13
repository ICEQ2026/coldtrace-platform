package com.acme.coldtrace.platform.maintenancemanagement.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.TechnicalServiceRequestCommandFailure;
import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.TechnicalServiceRequestCommandService;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.TechnicalServiceRequestRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service that handles technical service request command use cases.
 * <p>
 * The service coordinates the maintenance bounded context with identity, asset
 * management and alerts through anti-corruption facades. It protects aggregate
 * invariants before persistence and returns explicit command failures so the REST
 * layer can translate them into stable API responses.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class TechnicalServiceRequestCommandServiceImpl implements TechnicalServiceRequestCommandService {
    private final TechnicalServiceRequestRepository technicalServiceRequestRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final AlertsContextFacade alertsContextFacade;

    /**
     * Creates the command service with repositories and cross-context facades required
     * to validate service request creation and status transitions.
     *
     * @param technicalServiceRequestRepository repository for technical service requests
     * @param identityAccessContextFacade facade used to verify organization existence
     * @param assetManagementContextFacade facade used to verify and read assets
     * @param alertsContextFacade facade used to verify related incidents
     */
    public TechnicalServiceRequestCommandServiceImpl(
            TechnicalServiceRequestRepository technicalServiceRequestRepository,
            IdentityAccessContextFacade identityAccessContextFacade,
            AssetManagementContextFacade assetManagementContextFacade,
            AlertsContextFacade alertsContextFacade
    ) {
        this.technicalServiceRequestRepository = technicalServiceRequestRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.alertsContextFacade = alertsContextFacade;
    }

    /**
     * Handles a command that opens a technical service request for an asset.
     * <p>
     * The organization and asset are mandatory dependencies. The incident is optional,
     * but when present it must belong to the same organization to keep the request
     * traceable to a valid alert.
     *
     * @param command command containing the organization, asset, optional incident and issue description
     * @return successful result with the created request, or a command failure when a dependency is invalid
     */
    @Override
    @Transactional
    public Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(
            CreateTechnicalServiceRequestCommand command
    ) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.OrganizationNotFound());
        }

        var asset = assetManagementContextFacade.fetchAssetByIdAndOrganizationId(
                command.organizationId(),
                command.assetId()
        );
        if (asset.isEmpty()) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.AssetNotFound());
        }

        if (command.incidentId() != null) {
            var incident = alertsContextFacade.fetchIncidentByIdAndOrganizationId(
                    command.organizationId(),
                    command.incidentId()
            );
            if (incident.isEmpty()) {
                return Result.failure(new TechnicalServiceRequestCommandFailure.IncidentNotFound());
            }
            if (incident.get().assetId() == null || !command.assetId().equals(incident.get().assetId())) {
                return Result.failure(new TechnicalServiceRequestCommandFailure.InconsistentIncidentReference());
            }
            if (hasActiveRequestForIncident(command.organizationId(), command.incidentId())) {
                log.warn("Duplicate active technical service request rejected: organizationId={}, incidentId={}",
                        command.organizationId(), command.incidentId());
                return Result.failure(new TechnicalServiceRequestCommandFailure.DuplicateActiveIncidentRequest());
            }
        }

        var request = technicalServiceRequestRepository.save(new TechnicalServiceRequest(command, asset.orElseThrow()));
        log.info(
                "Technical service request created: id={}, organizationId={}, assetId={}",
                request.getId(),
                request.getOrganizationId(),
                request.getAssetId()
        );

        return Result.success(request);
    }

    /**
     * Handles a command that changes the lifecycle status of a technical service request.
     * <p>
     * Closing a request requires closure summary, evidence and the actor that closed it.
     * Other transitions are checked by the aggregate to keep lifecycle rules in one place.
     *
     * @param command command containing the target status and optional closure data
     * @return successful result with the updated request, or a command failure when the transition is invalid
     */
    @Override
    @Transactional
    public Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(
            UpdateTechnicalServiceRequestStatusCommand command
    ) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.OrganizationNotFound());
        }

        var request = technicalServiceRequestRepository.findByIdAndOrganizationId(
                command.technicalServiceRequestId(),
                command.organizationId()
        );
        if (request.isEmpty()) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.RequestNotFound());
        }

        if (!request.get().canTransitionTo(command.status())) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.InvalidTransition());
        }

        if ("closed".equals(command.status()) && missingClosureData(command)) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.MissingClosureData());
        }

        request.get().updateStatus(command);
        var updatedRequest = technicalServiceRequestRepository.save(request.get());
        return Result.success(updatedRequest);
    }

    /**
     * Checks whether a close transition is missing mandatory closure evidence.
     *
     * @param command status update command to inspect
     * @return {@code true} when at least one required closure field is blank
     */
    private boolean missingClosureData(UpdateTechnicalServiceRequestStatusCommand command) {
        return isBlank(command.closedBy()) || isBlank(command.closureSummary()) || isBlank(command.evidence());
    }

    /**
     * Checks whether a string value is null, empty or only whitespace.
     *
     * @param value value to inspect
     * @return {@code true} when the value is blank
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean hasActiveRequestForIncident(Long organizationId, Long incidentId) {
        return technicalServiceRequestRepository.findAllByOrganizationId(organizationId).stream()
                .filter(TechnicalServiceRequest::isActive)
                .anyMatch(request -> incidentId.equals(request.getIncidentId()));
    }
}
