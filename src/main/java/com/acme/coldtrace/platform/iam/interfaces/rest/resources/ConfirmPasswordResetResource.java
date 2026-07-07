package com.acme.coldtrace.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request resource used to confirm a password reset.
 *
 * @param token password reset token received by email
 * @param password new password
 * @since 1.0
 */
@Schema(
        name = "ConfirmPasswordReset",
        description = "Password reset confirmation payload"
)
public record ConfirmPasswordResetResource(
        @NotBlank(message = "is required")
        @Schema(description = "One-time password reset token from the recovery email")
        String token,

        @NotBlank(message = "is required")
        @Size(min = 8, message = "must have at least 8 characters")
        @Schema(description = "New password", example = "ColdTrace123")
        String password
) {
}
