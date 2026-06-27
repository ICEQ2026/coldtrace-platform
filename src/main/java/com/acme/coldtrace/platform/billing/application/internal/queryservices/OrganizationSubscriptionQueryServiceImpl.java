package com.acme.coldtrace.platform.billing.application.internal.queryservices;

import com.acme.coldtrace.platform.billing.application.internal.services.EntitlementPolicyService;
import com.acme.coldtrace.platform.billing.application.internal.services.OrganizationSubscriptionUsageService;
import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionDetails;
import com.acme.coldtrace.platform.billing.application.queryservices.OrganizationSubscriptionQueryFailure;
import com.acme.coldtrace.platform.billing.application.queryservices.OrganizationSubscriptionQueryService;
import com.acme.coldtrace.platform.billing.domain.model.queries.GetOrganizationSubscriptionByOrganizationIdQuery;
import com.acme.coldtrace.platform.billing.domain.repositories.OrganizationSubscriptionRepository;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for organization subscription queries.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OrganizationSubscriptionQueryServiceImpl implements OrganizationSubscriptionQueryService {
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final IamContextFacade iamContextFacade;
    private final OrganizationSubscriptionUsageService usageService;
    private final EntitlementPolicyService entitlementPolicyService;

    public OrganizationSubscriptionQueryServiceImpl(
            OrganizationSubscriptionRepository organizationSubscriptionRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            IamContextFacade iamContextFacade,
            OrganizationSubscriptionUsageService usageService,
            EntitlementPolicyService entitlementPolicyService
    ) {
        this.organizationSubscriptionRepository = organizationSubscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.iamContextFacade = iamContextFacade;
        this.usageService = usageService;
        this.entitlementPolicyService = entitlementPolicyService;
    }

    /**
     * Retrieves the organization subscription and computes current entitlements.
     *
     * @param query query containing the organization identifier
     * @return subscription details or query failure
     */
    @Override
    @Transactional(readOnly = true)
    public Result<OrganizationSubscriptionDetails, OrganizationSubscriptionQueryFailure> handle(
            GetOrganizationSubscriptionByOrganizationIdQuery query
    ) {
        if (!iamContextFacade.organizationExists(query.organizationId())) {
            log.warn("Organization not found for subscription query: organizationId={}", query.organizationId());
            return Result.failure(new OrganizationSubscriptionQueryFailure.OrganizationNotFound());
        }

        var subscription = organizationSubscriptionRepository.findByOrganizationId(query.organizationId())
                .orElse(null);
        if (subscription == null) {
            log.warn("Organization subscription not found: organizationId={}", query.organizationId());
            return Result.failure(new OrganizationSubscriptionQueryFailure.OrganizationSubscriptionNotFound());
        }
        var plan = subscriptionPlanRepository.findByCode(subscription.getPlanCode());
        if (plan.isEmpty()) {
            log.warn("Subscription plan not found for organization subscription: organizationId={}, planCode={}",
                    query.organizationId(), subscription.getPlanCode());
            return Result.failure(
                    new OrganizationSubscriptionQueryFailure.SubscriptionPlanNotFound(subscription.getPlanCode())
            );
        }

        var usage = usageService.snapshotFor(query.organizationId());
        var entitlements = entitlementPolicyService.compute(subscription, plan.get(), usage);
        return Result.success(new OrganizationSubscriptionDetails(subscription, plan.get(), usage, entitlements));
    }
}
