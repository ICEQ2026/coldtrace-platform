package com.acme.coldtrace.platform.identityaccess.application.commandservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;

public interface OrganizationCommandService {
    Organization handle(CreateOrganizationCommand command);
}
