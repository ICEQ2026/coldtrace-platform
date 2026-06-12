package com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands;

public record CreateTechnicalServiceRequestCommand(
        Long organizationId,
        Long assetId,
        Long incidentId,
        String issueDescription,
        String priority,
        String requestedBy
) {
}
