package com.acme.coldtrace.platform.billing.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.application.commandservices.OrganizationSubscriptionCommandService;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.model.commands.InitializeBaseOrganizationSubscriptionCommand;
import com.acme.coldtrace.platform.billing.domain.model.commands.SeedBaseOrganizationSubscriptionsCommand;
import com.acme.coldtrace.platform.billing.domain.repositories.OrganizationSubscriptionRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for organization subscription commands.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OrganizationSubscriptionCommandServiceImpl implements OrganizationSubscriptionCommandService {
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;
    private final IamContextFacade iamContextFacade;

    public OrganizationSubscriptionCommandServiceImpl(
            OrganizationSubscriptionRepository organizationSubscriptionRepository,
            IamContextFacade iamContextFacade
    ) {
        this.organizationSubscriptionRepository = organizationSubscriptionRepository;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OrganizationSubscription handle(InitializeBaseOrganizationSubscriptionCommand command) {
        return organizationSubscriptionRepository.findByOrganizationId(command.organizationId())
                .orElseGet(() -> initializeBaseSubscription(command.organizationId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void handle(SeedBaseOrganizationSubscriptionsCommand command) {
        var organizationIds = iamContextFacade.fetchOrganizationIds();
        organizationIds.forEach(organizationId ->
                handle(new InitializeBaseOrganizationSubscriptionCommand(organizationId)));
        log.debug("Verified Base subscriptions for {} organizations", organizationIds.size());
    }

    private OrganizationSubscription initializeBaseSubscription(Long organizationId) {
        var subscription = organizationSubscriptionRepository.save(new OrganizationSubscription(organizationId));
        log.info("Initialized Base subscription for organizationId={}", organizationId);
        return subscription;
    }
}
