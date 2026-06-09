package com.acme.coldtrace.platform.alerts.domain.model.aggregates;

import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationChannel;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Notification read model derived from incident lifecycle events.
 *
 * @since 1.0
 */
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification extends AbstractAggregateRoot<Notification> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    protected Notification() {
    }

    private Notification(Long organizationId, Long incidentId, NotificationChannel channel, String recipient, String message) {
        this.organizationId = organizationId;
        this.incidentId = incidentId;
        this.channel = channel;
        this.recipient = recipient;
        this.message = message;
        this.status = NotificationStatus.SENT;
        this.deliveredAt = Instant.now();
    }

    /**
     * Creates an application notification for a newly opened incident.
     *
     * @param incident source incident
     * @return notification read model
     */
    public static Notification incidentOpened(Incident incident) {
        return new Notification(
                incident.getOrganizationId(),
                incident.getId(),
                NotificationChannel.APP,
                null,
                "Incident %s opened with %s severity".formatted(incident.getId(), incident.getSeverity().name().toLowerCase())
        );
    }

    /**
     * Creates an application notification for an acknowledged incident.
     *
     * @param incident source incident
     * @return notification read model
     */
    public static Notification incidentAcknowledged(Incident incident) {
        return new Notification(
                incident.getOrganizationId(),
                incident.getId(),
                NotificationChannel.APP,
                incident.getAcknowledgedBy(),
                "Incident %s acknowledged".formatted(incident.getId())
        );
    }

    /**
     * Creates an application notification for a resolved incident.
     *
     * @param incident source incident
     * @return notification read model
     */
    public static Notification incidentResolved(Incident incident) {
        return new Notification(
                incident.getOrganizationId(),
                incident.getId(),
                NotificationChannel.APP,
                incident.getResolvedBy(),
                "Incident %s resolved".formatted(incident.getId())
        );
    }
}
