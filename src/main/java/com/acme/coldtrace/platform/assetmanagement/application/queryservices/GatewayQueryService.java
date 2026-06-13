package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetGatewayByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetGatewaysByOrganizationIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract providing read access to gateways.
 *
 * @since 1.0
 */
public interface GatewayQueryService {
    /**
     * Retrieves gateways by organization.
     *
     * @param query query object containing the organization identifier
     * @return gateways for the organization, possibly empty
     * @see GetGatewaysByOrganizationIdQuery
     */
    List<Gateway> handle(GetGatewaysByOrganizationIdQuery query);

    /**
     * Retrieves one gateway by id and organization.
     *
     * @param query query object containing organization and gateway identifiers
     * @return gateway when found, otherwise empty
     * @see GetGatewayByIdAndOrganizationIdQuery
     */
    Optional<Gateway> handle(GetGatewayByIdAndOrganizationIdQuery query);
}
