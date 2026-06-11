package com.acme.coldtrace.platform.assetmanagement.domain.exceptions;

/**
 * Exception raised when an asset management value object receives invalid data.
 * <p>
 * The message is intentionally suitable for the shared REST error handler: it
 * can be either a human-readable invariant message or an i18n message key when
 * the invalid value comes from an API command.
 *
 * @since 1.0
 */
public class InvalidAssetManagementValueException extends IllegalArgumentException {
    /**
     * Creates the exception.
     *
     * @param message validation message or i18n key
     */
    public InvalidAssetManagementValueException(String message) {
        super(message);
    }
}
