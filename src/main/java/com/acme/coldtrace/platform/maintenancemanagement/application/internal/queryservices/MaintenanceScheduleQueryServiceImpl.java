package com.acme.coldtrace.platform.maintenancemanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.application.queryservices.MaintenanceScheduleQueryFailure;
import com.acme.coldtrace.platform.maintenancemanagement.application.queryservices.MaintenanceScheduleQueryService;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetMaintenanceScheduleByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetMaintenanceSchedulesByOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.MaintenanceScheduleRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for maintenance schedule query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class MaintenanceScheduleQueryServiceImpl implements MaintenanceScheduleQueryService {
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;

    public MaintenanceScheduleQueryServiceImpl(
            MaintenanceScheduleRepository maintenanceScheduleRepository,
            IdentityAccessContextFacade identityAccessContextFacade
    ) {
        this.maintenanceScheduleRepository = maintenanceScheduleRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
    }

    /**
     * Retrieves maintenance schedules for an organization.
     *
     * @param query query containing the organization identifier
     * @return success with schedules or failure with query error
     * @see GetMaintenanceSchedulesByOrganizationIdQuery
     */
    @Override
    public Result<List<MaintenanceSchedule>, MaintenanceScheduleQueryFailure> handle(
            GetMaintenanceSchedulesByOrganizationIdQuery query
    ) {
        if (!identityAccessContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new MaintenanceScheduleQueryFailure.OrganizationNotFound());
        }
        return Result.success(maintenanceScheduleRepository.findAllByOrganizationId(query.organizationId()));
    }

    /**
     * Retrieves one maintenance schedule by id and organization.
     *
     * @param query query containing schedule and organization identifiers
     * @return success with schedule or failure with query error
     * @see GetMaintenanceScheduleByIdAndOrganizationIdQuery
     */
    @Override
    public Result<MaintenanceSchedule, MaintenanceScheduleQueryFailure> handle(
            GetMaintenanceScheduleByIdAndOrganizationIdQuery query
    ) {
        if (!identityAccessContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new MaintenanceScheduleQueryFailure.OrganizationNotFound());
        }
        var schedule = maintenanceScheduleRepository.findByIdAndOrganizationId(
                query.maintenanceScheduleId(),
                query.organizationId()
        );
        if (schedule.isEmpty()) {
            return Result.failure(new MaintenanceScheduleQueryFailure.MaintenanceScheduleNotFound());
        }
        return Result.success(schedule.get());
    }
}
