package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.IoTDeviceUuid;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for the {@link IoTDeviceUuid} value object.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class IoTDeviceUuidPersistenceConverter implements AttributeConverter<IoTDeviceUuid, String> {
    /**
     * Converts the domain value object to its database representation.
     *
     * @param attribute IoT device uuid value object
     * @return database text value
     */
    @Override
    public String convertToDatabaseColumn(IoTDeviceUuid attribute) {
        return attribute == null ? null : attribute.value();
    }

    /**
     * Converts a database value into an IoT device uuid value object.
     *
     * @param dbData database text value
     * @return IoT device uuid value object
     */
    @Override
    public IoTDeviceUuid convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new IoTDeviceUuid(dbData);
    }
}
