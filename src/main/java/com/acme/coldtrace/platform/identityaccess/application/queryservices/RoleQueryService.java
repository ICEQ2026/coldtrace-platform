package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllRolesQuery;

import java.util.List;

public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
}
