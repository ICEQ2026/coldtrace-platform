package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response resource returned after a password reset token is consumed.
 *
 * @param changed whether the password was changed
 * @since 1.0
 */
@Schema(
        name = "PasswordResetConfirmation",
        description = "Password reset confirmation response"
)
public record PasswordResetConfirmationResource(
        @Schema(description = "Whether the password was changed", example = "true")
        boolean changed
) {
}
