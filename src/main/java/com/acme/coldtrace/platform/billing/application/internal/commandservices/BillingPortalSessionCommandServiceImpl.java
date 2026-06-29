package com.acme.coldtrace.platform.billing.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingPortalSessionCommandFailure;
import com.acme.coldtrace.platform.billing.application.commandservices.BillingPortalSessionCommandService;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderFailure;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderRequest;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderService;
import com.acme.coldtrace.platform.billing.application.model.BillingPortalSession;
import com.acme.coldtrace.platform.billing.domain.model.commands.CreateBillingPortalSessionCommand;
import com.acme.coldtrace.platform.billing.domain.repositories.OrganizationSubscriptionRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Application service implementation for provider-hosted customer portal session creation.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class BillingPortalSessionCommandServiceImpl implements BillingPortalSessionCommandService {
    private final IamContextFacade iamContextFacade;
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;
    private final PortalSessionProviderService portalSessionProviderService;

    public BillingPortalSessionCommandServiceImpl(
            IamContextFacade iamContextFacade,
            OrganizationSubscriptionRepository organizationSubscriptionRepository,
            PortalSessionProviderService portalSessionProviderService
    ) {
        this.iamContextFacade = iamContextFacade;
        this.organizationSubscriptionRepository = organizationSubscriptionRepository;
        this.portalSessionProviderService = portalSessionProviderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<BillingPortalSession, BillingPortalSessionCommandFailure> handle(
            CreateBillingPortalSessionCommand command
    ) {
        if (!iamContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for portal session: organizationId={}", command.organizationId());
            return Result.failure(new BillingPortalSessionCommandFailure.OrganizationNotFound());
        }

        var subscription = organizationSubscriptionRepository.findByOrganizationId(command.organizationId());
        if (subscription.isEmpty()) {
            log.warn("Organization subscription not found for portal session: organizationId={}",
                    command.organizationId());
            return Result.failure(new BillingPortalSessionCommandFailure.OrganizationSubscriptionNotFound());
        }

        var providerCustomerId = subscription.get().getProviderCustomerId();
        if (providerCustomerId == null) {
            log.warn("Organization has no provider customer id for portal session: organizationId={}",
                    command.organizationId());
            return Result.failure(new BillingPortalSessionCommandFailure.ProviderCustomerNotFound());
        }

        var providerRequest = new PortalSessionProviderRequest(command.organizationId(), providerCustomerId);
        var providerResult = portalSessionProviderService.createCustomerPortalSession(providerRequest);
        return providerResult.fold(
                session -> Result.success(new BillingPortalSession(
                        session.provider(),
                        session.sessionId(),
                        session.portalUrl(),
                        command.organizationId()
                )),
                failure -> Result.failure(toCommandFailure(failure))
        );
    }

    private BillingPortalSessionCommandFailure toCommandFailure(PortalSessionProviderFailure failure) {
        if (failure == PortalSessionProviderFailure.NOT_CONFIGURED) {
            return new BillingPortalSessionCommandFailure.ProviderNotConfigured();
        }
        return new BillingPortalSessionCommandFailure.ProviderUnavailable();
    }
}
