package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllOrganizationsQuery;

import java.util.List;

public interface OrganizationQueryService {
    List<Organization> handle(GetAllOrganizationsQuery query);
}
