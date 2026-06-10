package com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects;

import jakarta.validation.constraints.Email;

/**
 * Value object that represents a validated email address.
 * <p>
 * Identity Access uses email values for organization contacts and users. The
 * value object normalizes the string and prevents blank emails from entering
 * the domain model.
 *
 * @param value email address string
 * @since 1.0
 */
public record EmailAddress(@Email String value) {
    /**
     * Creates a normalized email address.
     *
     * @param value email address string
     * @throws IllegalArgumentException when the value is null or blank
     */
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
        value = value.trim();
    }

    /**
     * Returns the email address string.
     *
     * @return email address string
     */
    public String getValue() {
        return value;
    }
}
