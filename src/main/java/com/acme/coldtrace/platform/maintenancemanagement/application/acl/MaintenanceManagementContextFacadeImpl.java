package com.acme.coldtrace.platform.maintenancemanagement.application.acl;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.MaintenanceScheduleRepository;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.TechnicalServiceRequestRepository;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Application-layer implementation of {@link MaintenanceManagementContextFacade}.
 *
 * @since 1.0
 */
@Service
public class MaintenanceManagementContextFacadeImpl implements MaintenanceManagementContextFacade {
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final TechnicalServiceRequestRepository technicalServiceRequestRepository;

    public MaintenanceManagementContextFacadeImpl(
            MaintenanceScheduleRepository maintenanceScheduleRepository,
            TechnicalServiceRequestRepository technicalServiceRequestRepository
    ) {
        this.maintenanceScheduleRepository = maintenanceScheduleRepository;
        this.technicalServiceRequestRepository = technicalServiceRequestRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MaintenanceScheduleSnapshot> fetchMaintenanceSchedulesByOrganizationIdAndAssetId(
            Long organizationId,
            Long assetId
    ) {
        if (assetId == null) {
            return List.of();
        }
        return maintenanceScheduleRepository.findAllByOrganizationId(organizationId).stream()
                .filter(schedule -> Objects.equals(schedule.getAssetId(), assetId))
                .map(this::toScheduleSnapshot)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TechnicalServiceRequestSnapshot> fetchTechnicalServiceRequestsByOrganizationIdAndIncidentId(
            Long organizationId,
            Long incidentId
    ) {
        if (incidentId == null) {
            return List.of();
        }
        return technicalServiceRequestRepository.findAllByOrganizationId(organizationId).stream()
                .filter(request -> Objects.equals(request.getIncidentId(), incidentId))
                .map(this::toTechnicalServiceRequestSnapshot)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TechnicalServiceRequestSnapshot> fetchTechnicalServiceRequestsByOrganizationIdAndAssetId(
            Long organizationId,
            Long assetId
    ) {
        if (assetId == null) {
            return List.of();
        }
        return technicalServiceRequestRepository.findAllByOrganizationId(organizationId).stream()
                .filter(request -> Objects.equals(request.getAssetId(), assetId))
                .map(this::toTechnicalServiceRequestSnapshot)
                .toList();
    }

    private MaintenanceScheduleSnapshot toScheduleSnapshot(MaintenanceSchedule schedule) {
        return new MaintenanceScheduleSnapshot(
                schedule.getId(),
                schedule.getOrganizationId(),
                schedule.getUuid(),
                schedule.getAssetId(),
                schedule.getScheduledDate(),
                schedule.getFrequencyDays(),
                schedule.getResponsibleUserId(),
                schedule.getObservations(),
                schedule.getStatus(),
                schedule.getRegisteredAt()
        );
    }

    private TechnicalServiceRequestSnapshot toTechnicalServiceRequestSnapshot(TechnicalServiceRequest request) {
        return new TechnicalServiceRequestSnapshot(
                request.getId(),
                request.getOrganizationId(),
                request.getCode(),
                request.getAssetId(),
                request.getAssetLocationId(),
                request.getAssetName(),
                request.getIncidentId(),
                request.getIssueDescription(),
                request.getPriority(),
                request.getStatus(),
                request.getRequestedBy(),
                request.getRequestedAt(),
                request.getClosedAt(),
                request.getClosureSummary(),
                request.getEvidence(),
                request.getClosedBy()
        );
    }
}
