package com.acme.coldtrace.platform.identityaccess.application.commandservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for user command operations.
 * It returns a Result so the interface layer can map business failures to
 * stable HTTP responses.
 *
 * @since 1.0
 */
public interface UserCommandService {
    /**
     * Handles user creation.
     *
     * @param command command containing user identity and relationship data
     * @return success with created user or failure with a user command error
     * @throws IllegalArgumentException if the command contains invalid user data
     * @see CreateUserCommand
     */
    Result<User, UserCommandFailure> handle(CreateUserCommand command);
}
