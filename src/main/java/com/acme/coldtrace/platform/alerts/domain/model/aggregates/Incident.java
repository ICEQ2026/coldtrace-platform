package com.acme.coldtrace.platform.alerts.domain.model.aggregates;

import com.acme.coldtrace.platform.alerts.domain.exceptions.InvalidIncidentSeverityException;
import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.CreateIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.alerts.domain.model.events.IncidentOpenedEvent;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.IncidentSeverity;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.IncidentStatus;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationStatus;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Incident aggregate for the alerts context.
 * It owns the lifecycle state for monitoring incidents registered by the backend.
 *
 * @since 1.0
 */
@Getter
public class Incident extends AbstractDomainAggregateRoot<Incident> {
    private Long id;
    private Long organizationId;
    private Long assetId;
    private Long deviceId;
    private Long readingId;
    private String assetName;
    private String deviceName;
    private String type;
    private IncidentSeverity severity;
    private IncidentStatus status;
    private String value;
    private Instant detectedAt;
    private Instant acknowledgedAt;
    private String acknowledgedBy;
    private Instant resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private NotificationStatus lastNotificationStatus;
    private Instant lastNotificationAt;
    private Integer notificationCount;

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
     * Rebuilds an incident aggregate from persistence state.
     *
     * @param id incident identifier assigned by persistence
     * @param organizationId organization that owns the incident
     * @param assetId optional asset identifier
     * @param deviceId optional device identifier
     * @param readingId optional reading identifier
     * @param assetName optional asset display name
     * @param deviceName optional device display name
     * @param type incident type
     * @param severity incident severity
     * @param status lifecycle status
     * @param value detected value
     * @param detectedAt detection timestamp
     * @param acknowledgedAt acknowledgement timestamp
     * @param acknowledgedBy acknowledgement actor
     * @param resolvedAt resolution timestamp
     * @param resolvedBy resolution actor
     * @param resolutionNotes resolution notes
     * @param lastNotificationStatus last notification status
     * @param lastNotificationAt last notification timestamp
     * @param notificationCount emitted notification count
     */
    public Incident(
            Long id,
            Long organizationId,
            Long assetId,
            Long deviceId,
            Long readingId,
            String assetName,
            String deviceName,
            String type,
            IncidentSeverity severity,
            IncidentStatus status,
            String value,
            Instant detectedAt,
            Instant acknowledgedAt,
            String acknowledgedBy,
            Instant resolvedAt,
            String resolvedBy,
            String resolutionNotes,
            NotificationStatus lastNotificationStatus,
            Instant lastNotificationAt,
            Integer notificationCount
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.assetId = assetId;
        this.deviceId = deviceId;
        this.readingId = readingId;
        this.assetName = assetName;
        this.deviceName = deviceName;
        this.type = type;
        this.severity = severity;
        this.status = status;
        this.value = value;
        this.detectedAt = detectedAt;
        this.acknowledgedAt = acknowledgedAt;
        this.acknowledgedBy = acknowledgedBy;
        this.resolvedAt = resolvedAt;
        this.resolvedBy = resolvedBy;
        this.resolutionNotes = resolutionNotes;
        this.lastNotificationStatus = lastNotificationStatus;
        this.lastNotificationAt = lastNotificationAt;
        this.notificationCount = notificationCount;
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

    /**
     * Registers the domain event emitted after a new incident is persisted.
     */
    public void onOpened() {
        registerDomainEvent(IncidentOpenedEvent.from(this));
    }

    private IncidentSeverity parseSeverity(String severity) {
        try {
            return IncidentSeverity.valueOf(severity);
        } catch (IllegalArgumentException exception) {
            throw new InvalidIncidentSeverityException();
        }
    }
}
