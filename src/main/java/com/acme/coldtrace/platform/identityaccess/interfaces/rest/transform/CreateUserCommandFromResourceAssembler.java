package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.CreateUserResource;

/**
 * Interface layer translator converting user creation resources to commands.
 *
 * @since 1.0
 */
public class CreateUserCommandFromResourceAssembler {
    /**
     * Converts a CreateUserResource to a CreateUserCommand.
     *
     * @param resource user creation request resource
     * @return create user command
     */
    public static CreateUserCommand toCommandFromResource(CreateUserResource resource) {
        return new CreateUserCommand(
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.organizationId(),
                resource.roleId()
        );
    }
}
