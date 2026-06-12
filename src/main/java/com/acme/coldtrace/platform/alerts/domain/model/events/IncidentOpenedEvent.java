package com.acme.coldtrace.platform.alerts.domain.model.events;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;

import java.time.Instant;

/**
 * Domain event raised when an incident is opened.
 *
 * @param incidentId incident identifier
 * @param organizationId owning organization identifier
 * @param assetId optional asset identifier
 * @param severity incident severity
 * @param detectedAt detection timestamp
 * @since 1.0
 */
public record IncidentOpenedEvent(
        Long incidentId,
        Long organizationId,
        Long assetId,
        String severity,
        Instant detectedAt
) {
    /**
     * Builds the event from an incident aggregate.
     *
     * @param incident source aggregate
     * @return incident-opened event
     */
    public static IncidentOpenedEvent from(Incident incident) {
        return new IncidentOpenedEvent(
                incident.getId(),
                incident.getOrganizationId(),
                incident.getAssetId(),
                incident.getSeverity().name(),
                incident.getDetectedAt()
        );
    }
}
