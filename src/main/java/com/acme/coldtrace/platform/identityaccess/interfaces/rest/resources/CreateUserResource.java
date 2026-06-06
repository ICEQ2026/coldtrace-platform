package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request resource used to create a user.
 *
 * @param id optional client-provided identifier ignored by the backend
 * @param uuid optional client-provided user code ignored during creation
 * @param organizationUserId optional client-provided organization user id ignored during creation
 * @param firstName user first name
 * @param lastName user last name
 * @param email user email address
 * @param organizationId organization identifier
 * @param roleId role identifier
 * @since 1.0
 */
public record CreateUserResource(
        Long id,
        String uuid,
        Long organizationUserId,

        @NotBlank
        String firstName,

        String lastName,

        @NotBlank
        @Email
        String email,

        @NotNull
        Long organizationId,

        @NotNull
        Long roleId
) {
}
