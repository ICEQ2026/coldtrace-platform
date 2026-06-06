package com.acme.coldtrace.platform.identityaccess.domain.model.commands;

public record CreateOrganizationCommand(
        String legalName,
        String commercialName,
        String taxId,
        String contactEmail
) {
    public CreateOrganizationCommand {
        legalName = requireNonBlank(legalName, "identity-access.organization.error.legalName.required");
        commercialName = requireNonBlank(commercialName, "identity-access.organization.error.commercialName.required");
        taxId = taxId == null ? "" : taxId.trim();
        contactEmail = requireNonBlank(contactEmail, "identity-access.organization.error.contactEmail.required")
                .toLowerCase();
        if (!contactEmail.contains("@")) {
            throw new IllegalArgumentException("identity-access.organization.error.contactEmail.invalid");
        }
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
