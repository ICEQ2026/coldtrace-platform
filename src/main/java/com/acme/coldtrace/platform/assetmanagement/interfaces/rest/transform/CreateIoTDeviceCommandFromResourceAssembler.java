package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateIoTDeviceCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.CreateIoTDeviceResource;

/**
 * Assembler that converts IoT device creation resources into commands.
 *
 * @since 1.0
 */
public class CreateIoTDeviceCommandFromResourceAssembler {
    /**
     * Converts a create resource and route organization into a command.
     *
     * @param resource HTTP request body
     * @param organizationId organization identifier from the route
     * @return command consumed by the application layer
     */
    public static CreateIoTDeviceCommand toCommandFromResource(
            CreateIoTDeviceResource resource,
            Long organizationId
    ) {
        return new CreateIoTDeviceCommand(
                organizationId,
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
