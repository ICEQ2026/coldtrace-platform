package com.acme.coldtrace.platform.monitoring.interfaces.rest.transform;

import com.acme.coldtrace.platform.monitoring.domain.model.commands.CreateSensorReadingCommand;
import com.acme.coldtrace.platform.monitoring.interfaces.rest.resources.CreateSensorReadingResource;

/**
 * Assembler that converts sensor reading creation resources into commands.
 *
 * @since 1.0
 */
public class CreateSensorReadingCommandFromResourceAssembler {
    /**
     * Converts a create resource and route organization into a command.
     *
     * @param resource HTTP request body
     * @param organizationId organization identifier from the route
     * @return command consumed by the application layer
     */
    public static CreateSensorReadingCommand toCommandFromResource(
            CreateSensorReadingResource resource,
            Long organizationId
    ) {
        return new CreateSensorReadingCommand(
                organizationId,
                resource.assetId(),
                resource.iotDeviceId(),
                resource.temperature(),
                resource.humidity(),
                resource.recordedAt(),
                resource.motionDetected(),
                resource.imageCaptured(),
                resource.batteryLevel(),
                resource.signalStrength()
        );
    }
}
