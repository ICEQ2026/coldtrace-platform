package com.acme.coldtrace.platform.alerts.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationChannel;
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
 * JPA persistence entity for incident notification read models.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "notifications")
public class NotificationPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    private String recipient;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    private Instant deliveredAt;
    private String failureReason;
}
