package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request resource used to sign up an organization and its first user.
 *
 * @param legalName organization legal name
 * @param commercialName organization commercial name
 * @param taxId optional tax identifier
 * @param contactEmail organization contact email
 * @param firstName first user first name
 * @param lastName first user last name
 * @param email first user email address
 * @since 1.0
 */
public record CreateOrganizationSignUpResource(
        @NotBlank
        String legalName,

        @NotBlank
        String commercialName,

        String taxId,

        @NotBlank
        @Email
        String contactEmail,

        @NotBlank
        String firstName,

        String lastName,

        @NotBlank
        @Email
        String email
) {
}
