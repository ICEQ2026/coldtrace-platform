package com.acme.coldtrace.platform.identityaccess.application.acl;

import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
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

    public IdentityAccessContextFacadeImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean organizationExists(Long organizationId) {
        return organizationId != null && organizationRepository.existsById(organizationId);
    }
}
