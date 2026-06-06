package com.acme.coldtrace.platform.identityaccess.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.application.queryservices.UserQueryService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllUsersQuery;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserQueryServicesImpl implements UserQueryService {
    private final UserRepository userRepository;

    public UserQueryServicesImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        log.debug("Querying all users");
        var users = userRepository.findAll();
        log.debug("Found {} users", users.size());
        return users;
    }
}
