package com.acme.coldtrace.platform.billing.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingCheckoutSessionCommandFailure;
import com.acme.coldtrace.platform.billing.application.commandservices.BillingCheckoutSessionCommandService;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderFailure;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderRequest;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderService;
import com.acme.coldtrace.platform.billing.application.model.BillingCheckoutSession;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.commands.CreateBillingCheckoutSessionCommand;
import com.acme.coldtrace.platform.billing.domain.repositories.OrganizationSubscriptionRepository;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Application service implementation for provider-hosted checkout session creation.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class BillingCheckoutSessionCommandServiceImpl implements BillingCheckoutSessionCommandService {
    private final IamContextFacade iamContextFacade;
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CheckoutSessionProviderService checkoutSessionProviderService;

    public BillingCheckoutSessionCommandServiceImpl(
            IamContextFacade iamContextFacade,
            OrganizationSubscriptionRepository organizationSubscriptionRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            CheckoutSessionProviderService checkoutSessionProviderService
    ) {
        this.iamContextFacade = iamContextFacade;
        this.organizationSubscriptionRepository = organizationSubscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.checkoutSessionProviderService = checkoutSessionProviderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<BillingCheckoutSession, BillingCheckoutSessionCommandFailure> handle(
            CreateBillingCheckoutSessionCommand command
    ) {
        if (!iamContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for checkout session: organizationId={}", command.organizationId());
            return Result.failure(new BillingCheckoutSessionCommandFailure.OrganizationNotFound());
        }

        var subscription = organizationSubscriptionRepository.findByOrganizationId(command.organizationId());
        if (subscription.isEmpty()) {
            log.warn("Organization subscription not found for checkout session: organizationId={}",
                    command.organizationId());
            return Result.failure(new BillingCheckoutSessionCommandFailure.OrganizationSubscriptionNotFound());
        }

        var targetPlan = subscriptionPlanRepository.findByCode(command.targetPlanCode());
        if (targetPlan.isEmpty() || !Boolean.TRUE.equals(targetPlan.get().getActive())) {
            log.warn("Target plan not found for checkout session: organizationId={}, planCode={}",
                    command.organizationId(), command.targetPlanCode());
            return Result.failure(new BillingCheckoutSessionCommandFailure.TargetPlanNotFound(
                    command.targetPlanCode()
            ));
        }

        var plan = targetPlan.get();
        if (!isPaidPlan(plan)) {
            log.warn("Free plan checkout rejected: organizationId={}, planCode={}",
                    command.organizationId(), command.targetPlanCode());
            return Result.failure(new BillingCheckoutSessionCommandFailure.FreePlanCheckoutNotAllowed(
                    command.targetPlanCode()
            ));
        }

        if (plan.getStripePriceId() == null) {
            log.warn("Plan has no Stripe price id configured: organizationId={}, planCode={}",
                    command.organizationId(), command.targetPlanCode());
            return Result.failure(new BillingCheckoutSessionCommandFailure.PlanProviderPriceNotConfigured(
                    command.targetPlanCode()
            ));
        }

        var providerRequest = new CheckoutSessionProviderRequest(
                command.organizationId(),
                plan.getCode(),
                plan.getStripePriceId(),
                subscription.get().getProviderCustomerId()
        );
        var providerResult = checkoutSessionProviderService.createSubscriptionCheckoutSession(providerRequest);
        return providerResult.fold(
                session -> Result.success(new BillingCheckoutSession(
                        session.provider(),
                        session.sessionId(),
                        session.checkoutUrl(),
                        plan.getCode()
                )),
                failure -> Result.failure(toCommandFailure(failure))
        );
    }

    private boolean isPaidPlan(SubscriptionPlan plan) {
        return plan.getMonthlyPriceCents() != null && plan.getMonthlyPriceCents() > 0;
    }

    private BillingCheckoutSessionCommandFailure toCommandFailure(CheckoutSessionProviderFailure failure) {
        if (failure == CheckoutSessionProviderFailure.NOT_CONFIGURED) {
            return new BillingCheckoutSessionCommandFailure.ProviderNotConfigured();
        }
        return new BillingCheckoutSessionCommandFailure.ProviderUnavailable();
    }
}
