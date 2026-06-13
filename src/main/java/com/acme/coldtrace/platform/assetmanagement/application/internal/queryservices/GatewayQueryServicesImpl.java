package com.acme.coldtrace.platform.assetmanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.GatewayQueryService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetGatewayByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetGatewaysByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.GatewayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for gateway query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class GatewayQueryServicesImpl implements GatewayQueryService {
    private final GatewayRepository gatewayRepository;

    public GatewayQueryServicesImpl(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    /**
     * Retrieves gateways from persistence by organization.
     *
     * @param query query object containing the organization identifier
     * @return gateways for the organization, possibly empty
     * @see GetGatewaysByOrganizationIdQuery
     */
    @Override
    public List<Gateway> handle(GetGatewaysByOrganizationIdQuery query) {
        log.debug("Querying gateways by organizationId={}", query.organizationId());
        var gateways = gatewayRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} gateways for organizationId={}", gateways.size(), query.organizationId());
        return gateways;
    }

    /**
     * Retrieves one gateway from persistence by id and organization.
     *
     * @param query query object containing organization and gateway identifiers
     * @return gateway when found, otherwise empty
     * @see GetGatewayByIdAndOrganizationIdQuery
     */
    @Override
    public Optional<Gateway> handle(GetGatewayByIdAndOrganizationIdQuery query) {
        log.debug("Querying gateway by organizationId={}, gatewayId={}",
                query.organizationId(), query.gatewayId());
        var gateway = gatewayRepository.findByIdAndOrganizationId(query.gatewayId(), query.organizationId());
        if (gateway.isEmpty()) {
            log.warn("Gateway not found: organizationId={}, gatewayId={}",
                    query.organizationId(), query.gatewayId());
        }
        return gateway;
    }
}
