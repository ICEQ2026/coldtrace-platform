package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.IncidentResource;

/**
 * Interface layer translator converting incident entities to resources.
 *
 * @since 1.0
 */
public class IncidentResourceFromEntityAssembler {
    /**
     * Converts an incident aggregate into a resource.
     *
     * @param incident incident aggregate
     * @return incident resource
     */
    public static IncidentResource toResourceFromEntity(Incident incident) {
        return new IncidentResource(
                incident.getId(),
                incident.getOrganizationId(),
                incident.getAssetId(),
                incident.getDeviceId(),
                incident.getReadingId(),
                incident.getAssetName(),
                incident.getDeviceName(),
                incident.getType(),
                incident.getSeverity().name().toLowerCase(),
                incident.getStatus().name().toLowerCase(),
                incident.getValue(),
                incident.getDetectedAt(),
                incident.getAcknowledgedAt(),
                incident.getAcknowledgedBy(),
                incident.getResolvedAt(),
                incident.getResolvedBy(),
                incident.getResolutionNotes(),
                incident.getLastNotificationStatus() == null ? null : incident.getLastNotificationStatus().name().toLowerCase(),
                incident.getLastNotificationAt(),
                incident.getNotificationCount()
        );
    }
}
