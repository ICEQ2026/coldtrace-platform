package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.IncidentSeverity;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.IncidentStatus;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationStatus;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for incidents.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "incidents")
public class IncidentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    private Long assetId;
    private Long deviceId;
    private Long readingId;
    private String assetName;
    private String deviceName;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    private String value;

    @Column(nullable = false)
    private Instant detectedAt;

    private Instant acknowledgedAt;
    private String acknowledgedBy;
    private Instant resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;

    @Enumerated(EnumType.STRING)
    private NotificationStatus lastNotificationStatus;

    private Instant lastNotificationAt;
    private Integer notificationCount;
}
