package com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects;

/**
 * Value object for an organization-scoped location name.
 * <p>
 * A location name is part of the business identity used by operators when
 * placing assets and gateways. It is modeled explicitly so the domain aggregate
 * cannot carry a blank name even though persistence stores the value in a
 * simple text column.
 *
 * @param value location name
 * @since 1.0
 */
public record LocationName(String value) {
    /**
     * Creates a normalized location name.
     *
     * @param value location name
     * @throws IllegalArgumentException when the value is null or blank
     */
    public LocationName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Location name must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the location name.
     *
     * @return location name
     */
    public String getValue() {
        return value;
    }
}
