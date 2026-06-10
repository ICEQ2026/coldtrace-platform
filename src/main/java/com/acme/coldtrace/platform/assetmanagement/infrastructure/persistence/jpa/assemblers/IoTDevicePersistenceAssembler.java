package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.IoTDevicePersistenceEntity;

import java.util.List;

/**
 * Assembler that translates IoT devices between domain and persistence models.
 *
 * @since 1.0
 */
public final class IoTDevicePersistenceAssembler {
    private IoTDevicePersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return IoT device aggregate rebuilt from persisted state
     */
    public static IoTDevice toDomainFromPersistence(IoTDevicePersistenceEntity entity) {
        return new IoTDevice(
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

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param iotDevice IoT device aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static IoTDevicePersistenceEntity toPersistenceFromDomain(IoTDevice iotDevice) {
        var entity = new IoTDevicePersistenceEntity();
        entity.setId(iotDevice.getId());
        copyDomainState(iotDevice, entity);
        return entity;
    }

    /**
     * Copies mutable IoT device state into an existing persistence entity.
     *
     * @param iotDevice source domain aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(IoTDevice iotDevice, IoTDevicePersistenceEntity entity) {
        entity.setOrganizationId(iotDevice.getOrganizationId());
        entity.setGatewayId(iotDevice.getGatewayId());
        entity.setUuid(iotDevice.getUuidValue());
        entity.setDeviceType(iotDevice.getDeviceType());
        entity.setModel(iotDevice.getModel());
        entity.setMeasurementType(iotDevice.getMeasurementType());
        entity.setMeasurementParameters(List.copyOf(iotDevice.getMeasurementParameters()));
        entity.setReadingFrequencySeconds(iotDevice.getReadingFrequencySeconds());
        entity.setAssetId(iotDevice.getAssetId());
        entity.setStatus(iotDevice.getStatus());
        entity.setCalibrationStatus(iotDevice.getCalibrationStatus());
        entity.setLastCalibrationDate(iotDevice.getLastCalibrationDate());
        entity.setNextCalibrationDate(iotDevice.getNextCalibrationDate());
    }
}
