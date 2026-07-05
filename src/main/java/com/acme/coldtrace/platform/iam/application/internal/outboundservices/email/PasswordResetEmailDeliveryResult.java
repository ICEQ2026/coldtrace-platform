package com.acme.coldtrace.platform.iam.application.internal.outboundservices.email;

/**
 * Password reset email delivery outcome.
 *
 * @since 1.0
 */
public enum PasswordResetEmailDeliveryResult {
    SENT,
    NOT_CONFIGURED,
    FAILED
}
