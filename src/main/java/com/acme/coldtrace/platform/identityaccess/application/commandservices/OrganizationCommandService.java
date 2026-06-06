package com.acme.coldtrace.platform.identityaccess.application.commandservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;

/**
 * Application service contract for organization command operations.
 * It exposes write operations used by the interface layer without leaking
 * persistence details into REST controllers.
 *
 * @since 1.0
 */
public interface OrganizationCommandService {
    /**
     * Handles organization creation.
     *
     * @param command command containing organization legal, commercial, and contact data
     * @return created organization aggregate
     * @throws IllegalArgumentException if the command contains invalid organization data
     * @see CreateOrganizationCommand
     */
    Organization handle(CreateOrganizationCommand command);
}
