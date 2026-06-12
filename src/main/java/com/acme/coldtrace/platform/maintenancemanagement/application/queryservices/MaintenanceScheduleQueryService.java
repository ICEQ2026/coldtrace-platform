package com.acme.coldtrace.platform.maintenancemanagement.application.queryservices;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetMaintenanceScheduleByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetMaintenanceSchedulesByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application query service contract for preventive maintenance schedules.
 *
 * @since 1.0
 */
public interface MaintenanceScheduleQueryService {
    /**
     * Handles retrieval of schedules by organization.
     *
     * @param query organization-scoped query
     * @return success with schedules or failure with query error
     */
    Result<List<MaintenanceSchedule>, MaintenanceScheduleQueryFailure> handle(
            GetMaintenanceSchedulesByOrganizationIdQuery query
    );

    /**
     * Handles retrieval of one schedule by id and organization.
     *
     * @param query schedule-scoped query
     * @return success with schedule or failure with query error
     */
    Result<MaintenanceSchedule, MaintenanceScheduleQueryFailure> handle(
            GetMaintenanceScheduleByIdAndOrganizationIdQuery query
    );
}
