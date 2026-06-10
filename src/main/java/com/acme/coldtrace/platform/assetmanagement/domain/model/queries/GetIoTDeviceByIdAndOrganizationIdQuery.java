package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving one IoT device inside an organization.
 *
 * @param organizationId organization identifier
 * @param iotDeviceId IoT device identifier
 * @since 1.0
 */
public record GetIoTDeviceByIdAndOrganizationIdQuery(Long organizationId, Long iotDeviceId) {
    /**
     * Validates query identifiers.
     *
     * @throws IllegalArgumentException when any identifier is missing or invalid
     */
    public GetIoTDeviceByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.iot-device.error.organizationId.invalid");
        }
        if (iotDeviceId == null || iotDeviceId <= 0) {
            throw new IllegalArgumentException("asset-management.iot-device.error.iotDeviceId.invalid");
        }
    }
}
