package com.acme.coldtrace.platform.identityaccess.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.application.queryservices.RoleQueryService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllRolesQuery;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleQueryServicesImpl implements RoleQueryService {
    private final RoleRepository roleRepository;

    public RoleQueryServicesImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> handle(GetAllRolesQuery query) {
        log.debug("Querying all roles");
        var roles = roleRepository.findAll();
        log.debug("Found {} roles", roles.size());
        return roles;
    }
}
