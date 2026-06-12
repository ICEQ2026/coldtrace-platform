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

@Slf4j
@Service
public class TechnicalServiceRequestCommandServiceImpl implements TechnicalServiceRequestCommandService {
    private final TechnicalServiceRequestRepository technicalServiceRequestRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final AlertsContextFacade alertsContextFacade;

    public TechnicalServiceRequestCommandServiceImpl(TechnicalServiceRequestRepository technicalServiceRequestRepository,
            IdentityAccessContextFacade identityAccessContextFacade,
            AssetManagementContextFacade assetManagementContextFacade,
            AlertsContextFacade alertsContextFacade) {
        this.technicalServiceRequestRepository = technicalServiceRequestRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.alertsContextFacade = alertsContextFacade;
    }

    @Override
    @Transactional
    public Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(CreateTechnicalServiceRequestCommand command) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.OrganizationNotFound());
        }
        var asset = assetManagementContextFacade.fetchAssetByIdAndOrganizationId(command.organizationId(), command.assetId());
        if (asset.isEmpty()) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.AssetNotFound());
        }
        if (command.incidentId() != null &&
                alertsContextFacade.fetchIncidentByIdAndOrganizationId(command.organizationId(), command.incidentId()).isEmpty()) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.IncidentNotFound());
        }
        var request = technicalServiceRequestRepository.save(new TechnicalServiceRequest(command, asset.orElseThrow()));
        log.info("Technical service request created: id={}, organizationId={}, assetId={}",
                request.getId(), request.getOrganizationId(), request.getAssetId());
        return Result.success(request);
    }

    @Override
    @Transactional
    public Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(UpdateTechnicalServiceRequestStatusCommand command) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.OrganizationNotFound());
        }
        if (!TechnicalServiceRequest.ALLOWED_STATUSES.contains(command.status())) {
            return Result.failure(new TechnicalServiceRequestCommandFailure.InvalidStatus());
        }
        var request = technicalServiceRequestRepository.findByIdAndOrganizationId(
                command.technicalServiceRequestId(), command.organizationId());
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

    private boolean missingClosureData(UpdateTechnicalServiceRequestStatusCommand command) {
        return isBlank(command.closedBy()) || isBlank(command.closureSummary()) || isBlank(command.evidence());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
