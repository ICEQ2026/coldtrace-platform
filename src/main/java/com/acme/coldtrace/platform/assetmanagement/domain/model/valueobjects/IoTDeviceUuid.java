package com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects;

/**
 * Value object that represents the business uuid assigned to an IoT device.
 * <p>
 * IoT device uuid values are operator-facing identifiers scoped by organization.
 * The aggregate stores this value object instead of a raw string so application
 * services and persistence adapters can share the same normalization rule.
 *
 * @param value IoT device business uuid
 * @since 1.0
 */
public record IoTDeviceUuid(String value) {
    /**
     * Creates a normalized IoT device uuid.
     *
     * @param value IoT device business uuid
     * @throws IllegalArgumentException when the value is null or blank
     */
    public IoTDeviceUuid {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IoT device uuid must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the IoT device uuid value.
     *
     * @return IoT device business uuid
     */
    public String getValue() {
        return value;
    }
}
