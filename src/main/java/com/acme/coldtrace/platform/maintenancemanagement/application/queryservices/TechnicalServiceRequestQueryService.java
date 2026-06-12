package com.acme.coldtrace.platform.maintenancemanagement.application.queryservices;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetTechnicalServiceRequestByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetTechnicalServiceRequestsByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;
import java.util.List;

public interface TechnicalServiceRequestQueryService {
    Result<List<TechnicalServiceRequest>, TechnicalServiceRequestQueryFailure> handle(GetTechnicalServiceRequestsByOrganizationIdQuery query);
    Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure> handle(GetTechnicalServiceRequestByIdAndOrganizationIdQuery query);
}
