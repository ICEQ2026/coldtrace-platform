package com.acme.coldtrace.platform.identityaccess.domain.model.commands;

public record CreateUserCommand(
        String uuid,
        Long organizationUserId,
        String firstName,
        String lastName,
        String email,
        Long organizationId,
        Long roleId
) {
    public CreateUserCommand {
        uuid = uuid == null || uuid.isBlank() ? null : uuid.trim();
        firstName = requireNonBlank(firstName, "identity-access.user.error.firstName.required");
        lastName = lastName == null ? "" : lastName.trim();
        email = requireNonBlank(email, "identity-access.user.error.email.required").toLowerCase();
        if (!email.contains("@")) {
            throw new IllegalArgumentException("identity-access.user.error.email.invalid");
        }
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.organizationId.invalid");
        }
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("identity-access.user.error.roleId.invalid");
        }
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }
}
