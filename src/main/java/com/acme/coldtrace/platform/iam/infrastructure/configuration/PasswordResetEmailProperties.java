package com.acme.coldtrace.platform.iam.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Password reset email integration properties.
 *
 * @param emailEnabled whether password reset emails can be sent
 * @param emailFrom sender email used by the SMTP provider
 * @param frontendResetUrl frontend route that consumes reset tokens
 * @since 1.0
 */
@ConfigurationProperties(prefix = "coldtrace.iam.password-reset")
public record PasswordResetEmailProperties(
        boolean emailEnabled,
        String emailFrom,
        String frontendResetUrl
) {
    public PasswordResetEmailProperties {
        emailFrom = normalize(emailFrom);
        frontendResetUrl = normalize(frontendResetUrl);
    }

    /**
     * Checks whether email delivery is enabled and externally configurable values are present.
     *
     * @return true when the adapter may attempt SMTP delivery
     */
    public boolean hasRequiredConfiguration() {
        return emailEnabled && emailFrom != null && frontendResetUrl != null;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
