package com.acme.coldtrace.platform.monitoring.interfaces.rest.transform;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.interfaces.rest.resources.SensorReadingResource;

/**
 * Assembler that converts sensor reading aggregates into REST resources.
 *
 * @since 1.0
 */
public class SensorReadingResourceFromEntityAssembler {
    /**
     * Converts a sensor reading aggregate into a resource.
     *
     * @param entity domain aggregate
     * @return REST resource with reading data
     */
    public static SensorReadingResource toResourceFromEntity(SensorReading entity) {
        return new SensorReadingResource(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getIotDeviceId(),
                entity.getGatewayId(),
                entity.getLocationId(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getOutOfRange(),
                entity.getOutOfRange(),
                entity.getRecordedAt(),
                entity.getMotionDetected(),
                entity.getImageCaptured(),
                entity.getBatteryLevel(),
                entity.getSignalStrength()
        );
    }
}
