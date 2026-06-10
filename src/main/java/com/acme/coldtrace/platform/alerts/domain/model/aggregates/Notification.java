package com.acme.coldtrace.platform.alerts.domain.model.aggregates;

import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationChannel;
import com.acme.coldtrace.platform.alerts.domain.model.valueobjects.NotificationStatus;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Notification read model derived from incident lifecycle events.
 *
 * @since 1.0
 */
@Getter
public class Notification extends AbstractDomainAggregateRoot<Notification> {
    private Long id;
    private Long organizationId;
    private Long incidentId;
    private NotificationChannel channel;
    private String recipient;
    private String message;
    private NotificationStatus status;
    private Instant createdAt;
    private Instant deliveredAt;
    private String failureReason;

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
     * Rebuilds a notification aggregate from persistence state.
     *
     * @param id notification identifier assigned by persistence
     * @param organizationId organization that owns the notification
     * @param incidentId source incident identifier
     * @param channel delivery channel
     * @param recipient optional recipient
     * @param message notification message
     * @param status delivery status
     * @param createdAt creation timestamp assigned by persistence
     * @param deliveredAt delivery timestamp
     * @param failureReason optional failure reason
     */
    public Notification(
            Long id,
            Long organizationId,
            Long incidentId,
            NotificationChannel channel,
            String recipient,
            String message,
            NotificationStatus status,
            Instant createdAt,
            Instant deliveredAt,
            String failureReason
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.incidentId = incidentId;
        this.channel = channel;
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.failureReason = failureReason;
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
