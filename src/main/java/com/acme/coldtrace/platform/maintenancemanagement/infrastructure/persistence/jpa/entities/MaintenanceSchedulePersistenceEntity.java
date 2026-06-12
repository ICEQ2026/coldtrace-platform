package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * JPA persistence entity for preventive maintenance schedules.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "maintenance_schedules")
public class MaintenanceSchedulePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private Long assetId;

    @Column(nullable = false)
    private OffsetDateTime scheduledDate;

    @Column
    private Integer frequencyDays;

    @Column
    private Long responsibleUserId;

    @Column(length = 1000)
    private String observations;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private OffsetDateTime registeredAt;
}
