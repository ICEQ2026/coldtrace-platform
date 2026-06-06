package com.acme.coldtrace.platform.identityaccess.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.application.queryservices.OrganizationQueryService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllOrganizationsQuery;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OrganizationQueryServicesImpl implements OrganizationQueryService {
    private final OrganizationRepository organizationRepository;

    public OrganizationQueryServicesImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public List<Organization> handle(GetAllOrganizationsQuery query) {
        log.debug("Querying all organizations");
        var organizations = organizationRepository.findAll();
        log.debug("Found {} organizations", organizations.size());
        return organizations;
    }
}
