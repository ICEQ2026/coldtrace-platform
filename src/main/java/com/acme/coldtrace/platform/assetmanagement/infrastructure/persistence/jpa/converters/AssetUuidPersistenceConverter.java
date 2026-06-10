package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.AssetUuid;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for the {@link AssetUuid} value object.
 * <p>
 * The database stores asset uuids as strings while the domain model exposes a
 * strongly typed value object. This converter keeps that translation inside the
 * persistence layer.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class AssetUuidPersistenceConverter implements AttributeConverter<AssetUuid, String> {
    /**
     * Converts the domain value object to its database representation.
     *
     * @param attribute asset uuid value object
     * @return string value stored in the database
     */
    @Override
    public String convertToDatabaseColumn(AssetUuid attribute) {
        return attribute == null ? null : attribute.value();
    }

    /**
     * Converts a database value into the domain value object.
     *
     * @param dbData string value read from the database
     * @return asset uuid value object
     */
    @Override
    public AssetUuid convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new AssetUuid(dbData);
    }
}
