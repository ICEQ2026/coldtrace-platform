package com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources;

/**
 * REST resource representing one factual dashboard metric used by the AI prompt.
 *
 * @param name metric name
 * @param value metric value
 * @param unit optional unit
 * @param description metric definition
 * @since 1.0
 */
public record DashboardAiSourceMetricResource(
        String name,
        String value,
        String unit,
        String description
) {
}
