package com.acme.coldtrace.platform.iam.application.commandservices;

/**
 * Application result returned after a password reset token is consumed.
 *
 * @param changed whether the password was changed
 * @since 1.0
 */
public record PasswordResetConfirmationCommandResult(boolean changed) {
}
