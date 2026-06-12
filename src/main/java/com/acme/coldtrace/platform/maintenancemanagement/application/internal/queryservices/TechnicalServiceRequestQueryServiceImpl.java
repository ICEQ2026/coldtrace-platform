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

@Service
public class TechnicalServiceRequestQueryServiceImpl implements TechnicalServiceRequestQueryService {
    private final TechnicalServiceRequestRepository technicalServiceRequestRepository;
    private final IdentityAccessContextFacade identityAccessContextFacade;

    public TechnicalServiceRequestQueryServiceImpl(TechnicalServiceRequestRepository technicalServiceRequestRepository,
            IdentityAccessContextFacade identityAccessContextFacade) {
        this.technicalServiceRequestRepository = technicalServiceRequestRepository;
        this.identityAccessContextFacade = identityAccessContextFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<TechnicalServiceRequest>, TechnicalServiceRequestQueryFailure> handle(GetTechnicalServiceRequestsByOrganizationIdQuery query) {
        if (!identityAccessContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new TechnicalServiceRequestQueryFailure.OrganizationNotFound());
        }
        return Result.success(technicalServiceRequestRepository.findAllByOrganizationId(query.organizationId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure> handle(GetTechnicalServiceRequestByIdAndOrganizationIdQuery query) {
        if (!identityAccessContextFacade.organizationExists(query.organizationId())) {
            return Result.failure(new TechnicalServiceRequestQueryFailure.OrganizationNotFound());
        }
        return technicalServiceRequestRepository.findByIdAndOrganizationId(query.technicalServiceRequestId(), query.organizationId())
                .<Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure>>map(Result::success)
                .orElseGet(() -> Result.failure(new TechnicalServiceRequestQueryFailure.RequestNotFound()));
    }
}
