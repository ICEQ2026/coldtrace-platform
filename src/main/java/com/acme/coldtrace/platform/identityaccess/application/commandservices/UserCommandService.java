package com.acme.coldtrace.platform.identityaccess.application.commandservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

public interface UserCommandService {
    Result<User, UserCommandFailure> handle(CreateUserCommand command);
}
