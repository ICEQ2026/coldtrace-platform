package com.acme.coldtrace.platform.iam.application.internal.outboundservices.email;

import java.time.Instant;

/**
 * Outbound port for password reset email delivery.
 *
 * @since 1.0
 */
public interface PasswordResetEmailDeliveryService {
    /**
     * Sends the password reset link to the provided recipient.
     *
     * @param recipientEmail recipient email
     * @param rawToken one-time reset token
     * @param expiresAt token expiration timestamp
     * @return delivery outcome
     */
    PasswordResetEmailDeliveryResult sendPasswordResetLink(
            String recipientEmail,
            String rawToken,
            Instant expiresAt
    );
}
