package com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateMaintenanceScheduleCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.events.MaintenanceScheduleCreatedEvent;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Preventive maintenance schedule aggregate.
 * <p>
 * A maintenance schedule represents planned work for an organization asset.
 * It keeps the asset reference, planned execution date, optional recurrence and
 * lifecycle status owned by the backend so the frontend does not invent
 * operational state.
 *
 * @since 1.0
 */
@Getter
public class MaintenanceSchedule extends AbstractDomainAggregateRoot<MaintenanceSchedule> {
    private static final Set<String> TERMINAL_STATUSES = Set.of("completed", "canceled");

    private Long id;
    private Long organizationId;
    private String uuid;
    private Long assetId;
    private OffsetDateTime scheduledDate;
    private Integer frequencyDays;
    private Long responsibleUserId;
    private String observations;
    private String status;
    private OffsetDateTime registeredAt;

    protected MaintenanceSchedule() {
    }

    /**
     * Creates a new preventive maintenance schedule from a command.
     *
     * @param command validated maintenance schedule creation command
     */
    public MaintenanceSchedule(CreateMaintenanceScheduleCommand command) {
        this.organizationId = command.organizationId();
        this.uuid = "MNT-" + UUID.randomUUID();
        this.assetId = command.assetId();
        this.scheduledDate = command.scheduledDate();
        this.frequencyDays = command.frequencyDays();
        this.responsibleUserId = command.responsibleUserId();
        this.observations = command.observations();
        this.status = command.status();
        this.registeredAt = OffsetDateTime.now();
    }

    /**
     * Rebuilds a maintenance schedule aggregate from persisted state.
     *
     * @param id persistence identifier
     * @param organizationId owning organization identifier
     * @param uuid public maintenance schedule code
     * @param assetId maintained asset identifier
     * @param scheduledDate planned maintenance date
     * @param frequencyDays optional recurrence cadence in days
     * @param responsibleUserId optional responsible user identifier
     * @param observations optional planning notes
     * @param status lifecycle status
     * @param registeredAt creation timestamp captured by the domain
     */
    public MaintenanceSchedule(
            Long id,
            Long organizationId,
            String uuid,
            Long assetId,
            OffsetDateTime scheduledDate,
            Integer frequencyDays,
            Long responsibleUserId,
            String observations,
            String status,
            OffsetDateTime registeredAt
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.uuid = uuid;
        this.assetId = assetId;
        this.scheduledDate = scheduledDate;
        this.frequencyDays = frequencyDays;
        this.responsibleUserId = responsibleUserId;
        this.observations = observations;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    /**
     * Indicates whether this schedule is still active for duplicate checks.
     *
     * @return true when the schedule can still consume operational planning capacity
     */
    public boolean isActive() {
        return !TERMINAL_STATUSES.contains(status);
    }

    /**
     * Indicates whether the current lifecycle status can transition to the
     * requested status.
     *
     * @param requestedStatus requested lifecycle status
     * @return true when the transition is allowed
     */
    public boolean canTransitionTo(String requestedStatus) {
        if (status.equals(requestedStatus)) {
            return true;
        }
        if (TERMINAL_STATUSES.contains(status)) {
            return false;
        }
        if ("scheduled".equals(status)) {
            return Set.of("in_progress", "completed", "canceled").contains(requestedStatus);
        }
        if ("in_progress".equals(status)) {
            return Set.of("completed", "canceled").contains(requestedStatus);
        }
        return false;
    }

    /**
     * Updates the lifecycle status after the application service has validated
     * the transition.
     *
     * @param requestedStatus new lifecycle status
     */
    public void updateStatus(String requestedStatus) {
        this.status = requestedStatus;
    }

    /**
     * Registers the domain event emitted after a schedule is created.
     */
    public void onCreated() {
        registerDomainEvent(MaintenanceScheduleCreatedEvent.from(this));
    }
}
