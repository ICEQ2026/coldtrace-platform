package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateIoTDeviceCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.UpdateIoTDeviceResource;

/**
 * Assembler that converts IoT device update resources into commands.
 *
 * @since 1.0
 */
public class UpdateIoTDeviceCommandFromResourceAssembler {
    /**
     * Converts an update resource and route identifiers into a command.
     *
     * @param resource HTTP request body
     * @param organizationId organization identifier from the route
     * @param iotDeviceId IoT device identifier from the route
     * @return command consumed by the application layer
     */
    public static UpdateIoTDeviceCommand toCommandFromResource(
            UpdateIoTDeviceResource resource,
            Long organizationId,
            Long iotDeviceId
    ) {
        return new UpdateIoTDeviceCommand(
                organizationId,
                iotDeviceId,
                resource.gatewayId(),
                resource.uuid(),
                resource.deviceType(),
                resource.model(),
                resource.measurementType(),
                resource.measurementParameters(),
                resource.readingFrequencySeconds(),
                resource.assetId(),
                resource.status(),
                resource.calibrationStatus(),
                resource.lastCalibrationDate(),
                resource.nextCalibrationDate()
        );
    }
}
