package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

/**
 * Request resource used to manually register an incident.
 *
 * @param assetId optional asset identifier
 * @param deviceId optional device identifier
 * @param readingId optional sensor reading identifier
 * @param assetName optional asset display name
 * @param deviceName optional device display name
 * @param type incident type
 * @param severity incident severity
 * @param value detected or reported value
 * @since 1.0
 */
public record CreateIncidentResource(
        Long assetId,
        Long deviceId,
        Long readingId,
        String assetName,
        String deviceName,
        String type,
        String severity,
        String value
) {
}
