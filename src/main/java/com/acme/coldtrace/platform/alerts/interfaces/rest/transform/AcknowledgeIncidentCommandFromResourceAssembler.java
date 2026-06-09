package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.domain.model.commands.AcknowledgeIncidentCommand;
import com.acme.coldtrace.platform.alerts.interfaces.rest.resources.AcknowledgeIncidentResource;

/**
 * Interface layer translator converting acknowledgement resources to commands.
 *
 * @since 1.0
 */
public class AcknowledgeIncidentCommandFromResourceAssembler {
    /**
     * Converts an acknowledgement request into a command.
     *
     * @param resource acknowledgement request resource
     * @param organizationId organization identifier from the route
     * @param incidentId incident identifier from the route
     * @return acknowledgement command
     */
    public static AcknowledgeIncidentCommand toCommandFromResource(
            AcknowledgeIncidentResource resource,
            Long organizationId,
            Long incidentId
    ) {
        return new AcknowledgeIncidentCommand(organizationId, incidentId, resource.acknowledgedBy());
    }
}
