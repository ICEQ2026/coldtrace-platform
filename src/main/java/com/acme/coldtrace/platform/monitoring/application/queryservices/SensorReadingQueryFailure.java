package com.acme.coldtrace.platform.monitoring.application.queryservices;

/**
 * Failure types for sensor reading query execution.
 *
 * @since 1.0
 */
public sealed interface SensorReadingQueryFailure
        permits SensorReadingQueryFailure.OrganizationNotFound,
        SensorReadingQueryFailure.SensorReadingNotFound {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements SensorReadingQueryFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.organization-not-found"; }
    }

    /** Sensor reading not found failure. */
    record SensorReadingNotFound() implements SensorReadingQueryFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.sensor-reading-not-found"; }
    }
}
