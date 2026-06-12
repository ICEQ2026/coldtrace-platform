package com.acme.coldtrace.platform.maintenancemanagement.application.queryservices;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetTechnicalServiceRequestByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetTechnicalServiceRequestsByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;
import java.util.List;

/**
 * Application query service contract for technical service requests.
 *
 * @since 1.0
 */
public interface TechnicalServiceRequestQueryService {
    /**
     * Handles retrieval of service requests by organization.
     *
     * @param query organization-scoped query
     * @return success with requests or failure with query error
     */
    Result<List<TechnicalServiceRequest>, TechnicalServiceRequestQueryFailure> handle(
            GetTechnicalServiceRequestsByOrganizationIdQuery query
    );

    /**
     * Handles retrieval of one service request by id and organization.
     *
     * @param query request-scoped query
     * @return success with request or failure with query error
     */
    Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure> handle(
            GetTechnicalServiceRequestByIdAndOrganizationIdQuery query
    );
}
