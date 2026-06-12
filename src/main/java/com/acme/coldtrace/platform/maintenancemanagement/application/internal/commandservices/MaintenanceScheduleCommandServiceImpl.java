package com.acme.coldtrace.platform.maintenancemanagement.application.internal.commandservices;

import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.MaintenanceScheduleCommandFailure;
import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.MaintenanceScheduleCommandService;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateMaintenanceScheduleCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateMaintenanceScheduleStatusCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.MaintenanceScheduleRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for maintenance schedule command operations.
 * <p>
 * The service coordinates organization and asset ownership checks through ACL
 * facades before creating or mutating maintenance schedules. This keeps the
 * maintenance management bounded context independent from asset persistence
 * details while still enforcing cross-organization boundaries.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class MaintenanceScheduleCommandServiceImpl implements MaintenanceScheduleCommandService {
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;

    public MaintenanceScheduleCommandServiceImpl(
            MaintenanceScheduleRepository maintenanceScheduleRepository,
            IdentityAccessContextFacade identityAccessContextFacade,
            AssetManagementContextFacade assetManagementContextFacade
    ) {
        this.maintenanceScheduleRepository = maintenanceScheduleRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
    }

    /**
     * Handles preventive maintenance schedule creation.
     *
     * @param command command containing organization, asset and schedule data
     * @return success with persisted schedule or failure with command error
     * @see CreateMaintenanceScheduleCommand
     */
    @Override
    @Transactional
    public Result<MaintenanceSchedule, MaintenanceScheduleCommandFailure> handle(
            CreateMaintenanceScheduleCommand command
    ) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for maintenance schedule creation: organizationId={}",
                    command.organizationId());
            return Result.failure(new MaintenanceScheduleCommandFailure.OrganizationNotFound());
        }
        if (assetManagementContextFacade
                .fetchAssetByIdAndOrganizationId(command.organizationId(), command.assetId())
                .isEmpty()) {
            log.warn("Asset not found for maintenance schedule creation: organizationId={}, assetId={}",
                    command.organizationId(), command.assetId());
            return Result.failure(new MaintenanceScheduleCommandFailure.AssetNotFound());
        }
        if (hasActiveScheduleForAsset(command.organizationId(), command.assetId())) {
            log.warn("Duplicate active maintenance schedule rejected: organizationId={}, assetId={}",
                    command.organizationId(), command.assetId());
            return Result.failure(new MaintenanceScheduleCommandFailure.DuplicateActiveSchedule());
        }

        var schedule = new MaintenanceSchedule(command);
        var saved = maintenanceScheduleRepository.save(schedule);
        log.info("Maintenance schedule created: id={}, organizationId={}, assetId={}, status={}",
                saved.getId(), saved.getOrganizationId(), saved.getAssetId(), saved.getStatus());
        return Result.success(saved);
    }

    /**
     * Handles maintenance schedule status updates.
     *
     * @param command command containing the target schedule and lifecycle status
     * @return success with updated schedule or failure with command error
     * @see UpdateMaintenanceScheduleStatusCommand
     */
    @Override
    @Transactional
    public Result<MaintenanceSchedule, MaintenanceScheduleCommandFailure> handle(
            UpdateMaintenanceScheduleStatusCommand command
    ) {
        if (!identityAccessContextFacade.organizationExists(command.organizationId())) {
            return Result.failure(new MaintenanceScheduleCommandFailure.OrganizationNotFound());
        }
        var schedule = maintenanceScheduleRepository.findByIdAndOrganizationId(
                command.maintenanceScheduleId(),
                command.organizationId()
        );
        if (schedule.isEmpty()) {
            return Result.failure(new MaintenanceScheduleCommandFailure.MaintenanceScheduleNotFound());
        }
        var target = schedule.get();
        if (!target.canTransitionTo(command.status())) {
            return Result.failure(new MaintenanceScheduleCommandFailure.InvalidStatusTransition());
        }
        target.updateStatus(command.status());
        var saved = maintenanceScheduleRepository.save(target);
        log.info("Maintenance schedule status updated: id={}, organizationId={}, status={}",
                saved.getId(), saved.getOrganizationId(), saved.getStatus());
        return Result.success(saved);
    }

    private boolean hasActiveScheduleForAsset(Long organizationId, Long assetId) {
        return maintenanceScheduleRepository.findAllByOrganizationId(organizationId).stream()
                .filter(MaintenanceSchedule::isActive)
                .anyMatch(schedule -> assetId.equals(schedule.getAssetId()));
    }
}
