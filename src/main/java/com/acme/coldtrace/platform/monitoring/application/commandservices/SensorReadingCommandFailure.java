package com.acme.coldtrace.platform.monitoring.application.commandservices;

/**
 * Failure types for sensor reading command execution.
 *
 * @since 1.0
 */
public sealed interface SensorReadingCommandFailure
        permits SensorReadingCommandFailure.OrganizationNotFound,
        SensorReadingCommandFailure.AssetNotFound,
        SensorReadingCommandFailure.IoTDeviceNotFound,
        SensorReadingCommandFailure.GatewayNotFound,
        SensorReadingCommandFailure.DeviceNotAssignedToAsset,
        SensorReadingCommandFailure.IncompatibleLocation,
        SensorReadingCommandFailure.DeviceOffline,
        SensorReadingCommandFailure.GatewayOffline,
        SensorReadingCommandFailure.AssetSettingsNotFound,
        SensorReadingCommandFailure.UnsupportedMeasurement,
        SensorReadingCommandFailure.NoEligibleDevices {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.organization-not-found"; }
    }

    /** Asset not found failure. */
    record AssetNotFound() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.asset-not-found"; }
    }

    /** IoT device not found failure. */
    record IoTDeviceNotFound() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.iot-device-not-found"; }
    }

    /** Gateway not found failure. */
    record GatewayNotFound() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.gateway-not-found"; }
    }

    /** Device not assigned to the requested asset failure. */
    record DeviceNotAssignedToAsset() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.device-not-assigned-to-asset"; }
    }

    /** Asset and gateway location mismatch failure. */
    record IncompatibleLocation() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.incompatible-location"; }
    }

    /** Device offline failure. */
    record DeviceOffline() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.device-offline"; }
    }

    /** Gateway offline failure. */
    record GatewayOffline() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.gateway-offline"; }
    }

    /** Asset settings not found failure. */
    record AssetSettingsNotFound() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.asset-settings-not-found"; }
    }

    /** Unsupported measurement failure. */
    record UnsupportedMeasurement() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.unsupported-measurement"; }
    }

    /** No eligible devices failure. */
    record NoEligibleDevices() implements SensorReadingCommandFailure {
        @Override public String messageKey() { return "monitoring.sensor-reading.error.no-eligible-devices"; }
    }
}
