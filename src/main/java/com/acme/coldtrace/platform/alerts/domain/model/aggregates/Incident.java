package com.acme.coldtrace.platform.alerts.domain.model.aggregates;

import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.IncidentSeverity;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.IncidentStatus;
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
 * Incident aggregate for the alerts context.
 * It owns the lifecycle state for monitoring incidents registered by the backend.
 *
 * @since 1.0
 */
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "incidents")
public class Incident extends AbstractAggregateRoot<Incident> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    protected Incident() {
    }

    /**
     * Creates an incident from a command.
     *
     * @param command command containing incident data
     * @see CreateIncidentCommand
     */
    public Incident(CreateIncidentCommand command) {
        this.organizationId = command.organizationId();
        this.assetId = command.assetId();
        this.deviceId = command.deviceId();
        this.readingId = command.readingId();
        this.assetName = command.assetName();
        this.deviceName = command.deviceName();
        this.type = command.type();
        this.severity = parseSeverity(command.severity());
        this.status = IncidentStatus.OPEN;
        this.value = command.value();
        this.detectedAt = Instant.now();
        this.notificationCount = 0;
    }

    /**
     * Acknowledges an open incident.
     *
     * @param command acknowledgement command
     */
    public void acknowledge(AcknowledgeIncidentCommand command) {
        this.status = IncidentStatus.ACKNOWLEDGED;
        this.acknowledgedAt = Instant.now();
        this.acknowledgedBy = command.acknowledgedBy();
    }

    /**
     * Resolves an open or acknowledged incident.
     *
     * @param command resolution command
     */
    public void resolve(ResolveIncidentCommand command) {
        this.status = IncidentStatus.RESOLVED;
        this.resolvedAt = Instant.now();
        this.resolvedBy = command.resolvedBy();
        this.resolutionNotes = command.resolutionNotes();
    }

    /**
     * Records that a notification read model was emitted for this incident.
     *
     * @param notificationStatus notification status
     */
    public void recordNotification(NotificationStatus notificationStatus) {
        this.lastNotificationStatus = notificationStatus;
        this.lastNotificationAt = Instant.now();
        this.notificationCount = this.notificationCount == null ? 1 : this.notificationCount + 1;
    }

    /** @return true when the incident has not been acknowledged or resolved */
    public boolean isOpen() {
        return IncidentStatus.OPEN.equals(this.status);
    }

    /** @return true when the incident has already been acknowledged */
    public boolean isAcknowledged() {
        return IncidentStatus.ACKNOWLEDGED.equals(this.status);
    }

    /** @return true when the incident has been resolved */
    public boolean isResolved() {
        return IncidentStatus.RESOLVED.equals(this.status);
    }

    private IncidentSeverity parseSeverity(String severity) {
        try {
            return IncidentSeverity.valueOf(severity);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("alerts.incident.error.severity.invalid");
        }
    }
}
