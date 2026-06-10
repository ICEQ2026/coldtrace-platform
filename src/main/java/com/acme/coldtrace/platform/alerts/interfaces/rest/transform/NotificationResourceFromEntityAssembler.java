package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.NotificationResource;

/**
 * Interface layer translator converting notification entities to resources.
 *
 * @since 1.0
 */
public class NotificationResourceFromEntityAssembler {
    /**
     * Converts a notification read model into a resource.
     *
     * @param notification notification read model
     * @return notification resource
     */
    public static NotificationResource toResourceFromEntity(Notification notification) {
        return new NotificationResource(
                notification.getId(),
                notification.getOrganizationId(),
                notification.getIncidentId(),
                notification.getChannel().name().toLowerCase(),
                notification.getRecipient(),
                notification.getMessage(),
                notification.getStatus().name().toLowerCase(),
                notification.getCreatedAt(),
                notification.getDeliveredAt(),
                notification.getFailureReason()
        );
    }
}
