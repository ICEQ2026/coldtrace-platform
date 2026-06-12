package com.acme.coldtrace.platform.maintenancemanagement.domain.repositories;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for preventive maintenance schedule aggregates.
 *
 * @since 1.0
 */
public interface MaintenanceScheduleRepository {
    /**
     * Finds maintenance schedules owned by an organization.
     *
     * @param organizationId organization identifier
     * @return organization maintenance schedules ordered by planned date
     */
    List<MaintenanceSchedule> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one maintenance schedule by id and organization.
     *
     * @param id maintenance schedule identifier
     * @param organizationId organization identifier
     * @return maintenance schedule when found
     */
    Optional<MaintenanceSchedule> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Persists a maintenance schedule aggregate.
     *
     * @param schedule schedule aggregate to persist
     * @return persisted schedule rebuilt from persistence state
     */
    MaintenanceSchedule save(MaintenanceSchedule schedule);
}
