package com.acme.coldtrace.platform.identityaccess.application.acl;

import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.identityaccess.interfaces.acl.IdentityAccessContextFacade;
import org.springframework.stereotype.Service;

/**
 * Application-layer implementation of {@link IdentityAccessContextFacade}.
 *
 * @since 1.0
 */
@Service
public class IdentityAccessContextFacadeImpl implements IdentityAccessContextFacade {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public IdentityAccessContextFacadeImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean organizationExists(Long organizationId) {
        return organizationId != null && organizationRepository.existsById(organizationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean userExistsByIdAndOrganizationId(Long organizationId, Long userId) {
        return organizationId != null &&
                userId != null &&
                userRepository.findByIdAndOrganizationId(userId, organizationId).isPresent();
    }
}
