package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
