package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.commands.ResolveIncidentCommand;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.ResolveIncidentResource;

/**
 * Interface layer translator converting resolution resources to commands.
 *
 * @since 1.0
 */
public class ResolveIncidentCommandFromResourceAssembler {
    /**
     * Converts a resolution request into a command.
     *
     * @param resource resolution request resource
     * @param organizationId organization identifier from the route
     * @param incidentId incident identifier from the route
     * @return resolution command
     */
    public static ResolveIncidentCommand toCommandFromResource(
            ResolveIncidentResource resource,
            Long organizationId,
            Long incidentId
    ) {
        return new ResolveIncidentCommand(
                organizationId,
                incidentId,
                resource.resolvedBy(),
                resource.resolutionNotes()
        );
    }
}
