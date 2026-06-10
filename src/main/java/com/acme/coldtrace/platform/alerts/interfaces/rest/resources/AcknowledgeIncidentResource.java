package com.acme.coldtrace.platform.alerts.interfaces.rest.resources;

/**
 * Request resource used to acknowledge an incident.
 *
 * @param acknowledgedBy actor that acknowledges the incident
 * @since 1.0
 */
public record AcknowledgeIncidentResource(String acknowledgedBy) {
}
