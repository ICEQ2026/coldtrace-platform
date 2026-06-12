package com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects;

import com.acme.coldtrace.platform.assetmanagement.domain.exceptions.InvalidAssetManagementValueException;

/**
 * Value object that represents the business uuid assigned to a gateway.
 *
 * @param value gateway business uuid
 * @since 1.0
 */
public record GatewayUuid(String value) {
    /**
     * Creates a normalized gateway uuid.
     *
     * @param value gateway business uuid
     * @throws InvalidAssetManagementValueException when the value is null or blank
     */
    public GatewayUuid {
        if (value == null || value.isBlank()) {
            throw new InvalidAssetManagementValueException("Gateway uuid must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the gateway uuid value.
     *
     * @return gateway business uuid
     */
    public String getValue() {
        return value;
    }
}
