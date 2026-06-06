package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllUsersQuery;

import java.util.List;

public interface UserQueryService {
    List<User> handle(GetAllUsersQuery query);
}
