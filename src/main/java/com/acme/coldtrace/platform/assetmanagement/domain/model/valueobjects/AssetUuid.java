package com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects;

import com.acme.coldtrace.platform.assetmanagement.domain.exceptions.InvalidAssetManagementValueException;

/**
 * Value object that represents the business uuid assigned to an asset.
 * <p>
 * The value is not the database identifier. It is the organization-scoped
 * business code used by operators and external integrations to recognize a
 * monitored cold-chain asset. The constructor trims the provided value and
 * rejects blank input so the aggregate never carries an invalid uuid.
 *
 * @param value string representation of the asset business uuid
 * @since 1.0
 */
public record AssetUuid(String value) {
    /**
     * Creates an asset uuid value object.
     *
     * @param value string representation of the asset business uuid
     * @throws InvalidAssetManagementValueException when the value is null or blank
     */
    public AssetUuid {
        if (value == null || value.isBlank()) {
            throw new InvalidAssetManagementValueException("Asset uuid must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the uuid value.
     *
     * @return string representation of the asset business uuid
     */
    public String getValue() {
        return value;
    }
}
