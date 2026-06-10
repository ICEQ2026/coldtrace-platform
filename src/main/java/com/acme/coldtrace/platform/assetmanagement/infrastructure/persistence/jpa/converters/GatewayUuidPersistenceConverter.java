package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.GatewayUuid;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for the {@link GatewayUuid} value object.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class GatewayUuidPersistenceConverter implements AttributeConverter<GatewayUuid, String> {
    /**
     * Converts the domain value object to its database representation.
     *
     * @param attribute gateway uuid value object
     * @return database text value
     */
    @Override
    public String convertToDatabaseColumn(GatewayUuid attribute) {
        return attribute == null ? null : attribute.value();
    }

    /**
     * Converts a database value into a gateway uuid value object.
     *
     * @param dbData database text value
     * @return gateway uuid value object
     */
    @Override
    public GatewayUuid convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new GatewayUuid(dbData);
    }
}
