package com.acme.coldtrace.platform.iam.domain.model.commands;

/**
 * Command for confirming a password reset with a one-time token.
 *
 * @param token password reset token received by email
 * @param password new raw password
 * @since 1.0
 */
public record ConfirmPasswordResetCommand(String token, String password) {
    private static final int MIN_PASSWORD_LENGTH = 8;

    public ConfirmPasswordResetCommand {
        token = requireNonBlank(token, "identity-access.password-reset.error.token.required").trim();
        requireNonBlank(password, "identity-access.password-reset.error.password.required");
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("identity-access.password-reset.error.password.invalid");
        }
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
