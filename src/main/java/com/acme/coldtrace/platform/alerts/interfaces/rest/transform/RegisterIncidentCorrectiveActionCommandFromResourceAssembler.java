package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.commands.RegisterIncidentCorrectiveActionCommand;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.RegisterIncidentCorrectiveActionResource;

/**
 * Interface layer translator converting corrective action resources to commands.
 *
 * @since 1.0
 */
public final class RegisterIncidentCorrectiveActionCommandFromResourceAssembler {
    private RegisterIncidentCorrectiveActionCommandFromResourceAssembler() {
    }

    /**
     * Converts a corrective action request into a command.
     *
     * @param resource corrective action request resource
     * @param organizationId organization identifier from the route
     * @param incidentId incident identifier from the route
     * @return corrective action command
     */
    public static RegisterIncidentCorrectiveActionCommand toCommandFromResource(
            RegisterIncidentCorrectiveActionResource resource,
            Long organizationId,
            Long incidentId
    ) {
        return new RegisterIncidentCorrectiveActionCommand(
                organizationId,
                incidentId,
                resource.correctiveAction(),
                resource.registeredBy()
        );
    }
}
