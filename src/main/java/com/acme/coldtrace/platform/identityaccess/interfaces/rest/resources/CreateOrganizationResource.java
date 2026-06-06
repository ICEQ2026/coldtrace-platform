package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to create an organization.
 *
 * @param id optional client-provided identifier ignored by the backend
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId optional tax identifier
 * @param contactEmail organization contact email
 * @since 1.0
 */
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
