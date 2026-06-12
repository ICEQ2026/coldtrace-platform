package com.acme.coldtrace.platform.maintenancemanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.application.queryservices.TechnicalServiceRequestQueryFailure;
import com.acme.coldtrace.platform.maintenancemanagement.application.queryservices.TechnicalServiceRequestQueryService;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetTechnicalServiceRequestByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries.GetTechnicalServiceRequestsByOrganizationIdQuery;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.TechnicalServiceRequestRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service that handles technical service request query use cases.
 * <p>
 * The service keeps read operations organization-aware. It validates the organization
 * through the identity and access ACL before reading maintenance data, avoiding leaks
 * from requests that belong to another tenant or to an organization that no longer
 * exists in the platform.
 *
 * @since 1.0
 */
@Service
public class TechnicalServiceRequestQueryServiceImpl implements TechnicalServiceRequestQueryService {
    private final TechnicalServiceRequestRepository technicalServiceRequestRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;

    /**
     * Creates the query service with the repository and cross-context facade required
     * to resolve organization-scoped read models.
     *
     * @param technicalServiceRequestRepository repository for technical service requests
     * @param identityAccessContextFacade facade used to verify organization existence
     */
    public TechnicalServiceRequestQueryServiceImpl(
            TechnicalServiceRequestRepository technicalServiceRequestRepository,
            IdentityAccessContextFacade identityAccessContextFacade
    ) {
        this.technicalServiceRequestRepository = technicalServiceRequestRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
    }

    /**
     * Handles a query that lists all technical service requests of an organization.
     *
     * @param query query containing the organization identifier
     * @return successful result with the organization requests, or a failure when the organization does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public Result<List<TechnicalServiceRequest>, TechnicalServiceRequestQueryFailure> handle(
            GetTechnicalServiceRequestsByOrganizationIdQuery query
    ) {
        if (!identityAccessContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new TechnicalServiceRequestQueryFailure.OrganizationNotFound());
        }
        return Result.success(technicalServiceRequestRepository.findAllByOrganizationId(query.organizationId()));
    }

    /**
     * Handles a query that retrieves a technical service request by id and organization.
     *
     * @param query query containing the request identifier and organization identifier
     * @return successful result with the request, or a failure when the organization or request does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure> handle(
            GetTechnicalServiceRequestByIdAndOrganizationIdQuery query
    ) {
        if (!identityAccessContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new TechnicalServiceRequestQueryFailure.OrganizationNotFound());
        }

        return technicalServiceRequestRepository.findByIdAndOrganizationId(
                        query.technicalServiceRequestId(),
                        query.organizationId()
                )
                .<Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure>>map(Result::success)
                .orElseGet(() -> Result.failure(new TechnicalServiceRequestQueryFailure.RequestNotFound()));
    }
}
