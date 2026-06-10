package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving IoT devices that belong to an organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetIoTDevicesByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     *
     * @throws IllegalArgumentException when the organization identifier is missing or invalid
     */
    public GetIoTDevicesByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.iot-device.error.organizationId.invalid");
        }
    }
}
