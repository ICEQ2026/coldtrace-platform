package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.commands.EscalateIncidentCommand;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.EscalateIncidentResource;

/**
 * Interface layer translator converting escalation resources to commands.
 *
 * @since 1.0
 */
public final class EscalateIncidentCommandFromResourceAssembler {
    private EscalateIncidentCommandFromResourceAssembler() {
    }

    /**
     * Converts an escalation request into a command.
     *
     * @param resource escalation request resource
     * @param organizationId organization identifier from the route
     * @param incidentId incident identifier from the route
     * @return escalation command
     */
    public static EscalateIncidentCommand toCommandFromResource(
            EscalateIncidentResource resource,
            Long organizationId,
            Long incidentId
    ) {
        return new EscalateIncidentCommand(
                organizationId,
                incidentId,
                resource.escalatedBy(),
                resource.escalationReason()
        );
    }
}
