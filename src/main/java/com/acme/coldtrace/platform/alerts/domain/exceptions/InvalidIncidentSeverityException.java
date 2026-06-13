package com.acme.coldtrace.platform.alerts.domain.exceptions;

/**
 * Exception raised when an incident receives an unsupported severity value.
 *
 * @since 1.0
 */
public class InvalidIncidentSeverityException extends IllegalArgumentException {
    /**
     * Creates the exception with the message key consumed by the REST layer.
     */
    public InvalidIncidentSeverityException() {
        super("alerts.incident.error.severity.invalid");
    }
}
