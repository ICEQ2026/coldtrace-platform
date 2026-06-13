package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.IoTDeviceResource;

import java.util.List;

/**
 * Assembler that converts IoT device aggregates into REST resources.
 *
 * @since 1.0
 */
public class IoTDeviceResourceFromEntityAssembler {
    /**
     * Converts an IoT device aggregate into a resource.
     *
     * @param entity domain aggregate
     * @return REST resource with device data
     */
    public static IoTDeviceResource toResourceFromEntity(IoTDevice entity) {
        return new IoTDeviceResource(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getGatewayId(),
                entity.getUuid(),
                entity.getDeviceType(),
                entity.getModel(),
                entity.getMeasurementType(),
                List.copyOf(entity.getMeasurementParameters()),
                entity.getReadingFrequencySeconds(),
                entity.getAssetId(),
                entity.getStatus(),
                entity.getCalibrationStatus(),
                entity.getLastCalibrationDate(),
                entity.getNextCalibrationDate()
        );
    }
}
