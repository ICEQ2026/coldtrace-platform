package com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources;

public record OrganizationResource(
        Long id,
        String legalName,
        String commercialName,
        String taxId,
        String contactEmail
) {
}
