package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

/**
 * Request resource used to resolve an incident.
 *
 * @param resolvedBy actor that resolves the incident
 * @param resolutionNotes resolution notes
 * @since 1.0
 */
public record ResolveIncidentResource(String resolvedBy, String resolutionNotes) {
}
