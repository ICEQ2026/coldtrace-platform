package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request resource used to create a user.
 *
 * @param firstName user first name
 * @param lastName user last name
 * @param email user email address
 * @param roleId role identifier
 * @since 1.0
 */
public record CreateUserResource(
        @NotBlank(message = "is required")
        @Schema(description = "User first name", example = "Maria")
        String firstName,
        String lastName,
        @NotBlank(message = "is required")
        @Email(message = "must be a valid email")
        @Schema(description = "User email address", example = "maria@coldtrace.test")
        String email,
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Role identifier assigned to the user", example = "2")
        Long roleId
) {
}
