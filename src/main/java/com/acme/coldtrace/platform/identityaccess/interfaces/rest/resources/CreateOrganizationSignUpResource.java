package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

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
        String legalName,

        String commercialName,

        String taxId,

        String contactEmail,

        String firstName,

        String lastName,

        String email
) {
}
