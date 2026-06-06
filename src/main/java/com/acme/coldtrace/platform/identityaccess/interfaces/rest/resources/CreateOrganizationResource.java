package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOrganizationResource(
        Long id,

        @NotBlank
        String legalName,

        @NotBlank
        String commercialName,

        String taxId,

        @NotBlank
        @Email
        String contactEmail
) {
}
