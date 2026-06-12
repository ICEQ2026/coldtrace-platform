package com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries;

public record GetTechnicalServiceRequestByIdAndOrganizationIdQuery(Long organizationId, Long technicalServiceRequestId) {
}
