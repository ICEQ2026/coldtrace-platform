package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
        @NotBlank
        String firstName,

        String lastName,

        @NotBlank
        @Email
        String email,

        @NotNull
        Long roleId
) {
}
