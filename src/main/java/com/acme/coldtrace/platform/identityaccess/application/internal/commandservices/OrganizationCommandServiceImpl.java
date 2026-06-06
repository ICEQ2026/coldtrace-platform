package com.acme.coldtrace.platform.identityaccess.application.internal.commandservices;

import com.acme.coldtrace.platform.identityaccess.application.commandservices.OrganizationCommandService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrganizationCommandServiceImpl implements OrganizationCommandService {
    private final OrganizationRepository organizationRepository;

    public OrganizationCommandServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional
    public Organization handle(CreateOrganizationCommand command) {
        var organization = organizationRepository.save(new Organization(command));
        log.info("Organization created: id={}, contactEmail={}", organization.getId(), organization.getContactEmail());
        return organization;
    }
}
