package com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands;

public record UpdateTechnicalServiceRequestStatusCommand(
        Long organizationId,
        Long technicalServiceRequestId,
        String status,
        String closureSummary,
        String evidence,
        String closedBy
) {
}
