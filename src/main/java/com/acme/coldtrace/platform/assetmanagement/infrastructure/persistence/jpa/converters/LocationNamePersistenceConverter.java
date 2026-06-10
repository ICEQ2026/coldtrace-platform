package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.LocationName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for the {@link LocationName} value object.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class LocationNamePersistenceConverter implements AttributeConverter<LocationName, String> {
    /**
     * Converts a location name value object to its database value.
     *
     * @param attribute location name value object
     * @return database text value
     */
    @Override
    public String convertToDatabaseColumn(LocationName attribute) {
        return attribute == null ? null : attribute.value();
    }

    /**
     * Converts a database value into a location name value object.
     *
     * @param dbData database text value
     * @return location name value object
     */
    @Override
    public LocationName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new LocationName(dbData);
    }
}
