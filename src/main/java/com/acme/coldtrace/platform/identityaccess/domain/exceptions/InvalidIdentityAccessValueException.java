package com.acme.coldtrace.platform.identityaccess.domain.exceptions;

/**
 * Exception raised when an identity access value object receives invalid data.
 *
 * @since 1.0
 */
public class InvalidIdentityAccessValueException extends IllegalArgumentException {
    /**
     * Creates the exception.
     *
     * @param message validation message or i18n key
     */
    public InvalidIdentityAccessValueException(String message) {
        super(message);
    }
}
