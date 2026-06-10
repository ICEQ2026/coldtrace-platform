package com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;

/**
 * Assembler that translates sensor readings between domain and persistence models.
 *
 * @since 1.0
 */
public final class SensorReadingPersistenceAssembler {
    private SensorReadingPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return sensor reading aggregate rebuilt from persisted state
     */
    public static SensorReading toDomainFromPersistence(SensorReadingPersistenceEntity entity) {
        return new SensorReading(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getIotDeviceId(),
                entity.getGatewayId(),
                entity.getLocationId(),
                entity.getTemperature(),
                entity.getHumidity(),
                entity.getOutOfRange(),
                entity.getRecordedAt(),
                entity.getMotionDetected(),
                entity.getImageCaptured(),
                entity.getBatteryLevel(),
                entity.getSignalStrength()
        );
    }

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param reading reading aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static SensorReadingPersistenceEntity toPersistenceFromDomain(SensorReading reading) {
        var entity = new SensorReadingPersistenceEntity();
        entity.setId(reading.getId());
        entity.setOrganizationId(reading.getOrganizationId());
        entity.setAssetId(reading.getAssetId());
        entity.setIotDeviceId(reading.getIotDeviceId());
        entity.setGatewayId(reading.getGatewayId());
        entity.setLocationId(reading.getLocationId());
        entity.setTemperature(reading.getTemperature());
        entity.setHumidity(reading.getHumidity());
        entity.setOutOfRange(reading.getOutOfRange());
        entity.setRecordedAt(reading.getRecordedAt());
        entity.setMotionDetected(reading.getMotionDetected());
        entity.setImageCaptured(reading.getImageCaptured());
        entity.setBatteryLevel(reading.getBatteryLevel());
        entity.setSignalStrength(reading.getSignalStrength());
        return entity;
    }
}
