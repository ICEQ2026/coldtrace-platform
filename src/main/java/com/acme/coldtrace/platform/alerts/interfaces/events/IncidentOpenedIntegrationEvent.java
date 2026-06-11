package com.acme.coldtrace.platform.alerts.interfaces.events;

import java.time.Instant;

/**
 * Integration event published by alerts when an incident is opened.
 *
 * @param incidentId incident identifier
 * @param organizationId owning organization identifier
 * @param assetId optional asset identifier
 * @param severity incident severity
 * @param detectedAt detection timestamp
 * @since 1.0
 */
public record IncidentOpenedIntegrationEvent(
        Long incidentId,
        Long organizationId,
        Long assetId,
        String severity,
        Instant detectedAt
) {
}
