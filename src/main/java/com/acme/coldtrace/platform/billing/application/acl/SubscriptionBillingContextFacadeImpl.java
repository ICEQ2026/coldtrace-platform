package com.acme.coldtrace.platform.billing.application.acl;

import com.acme.coldtrace.platform.billing.application.commandservices.OrganizationSubscriptionCommandService;
import com.acme.coldtrace.platform.billing.application.model.OrganizationEntitlement;
import com.acme.coldtrace.platform.billing.application.queryservices.OrganizationSubscriptionQueryService;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.commands.InitializeBaseOrganizationSubscriptionCommand;
import com.acme.coldtrace.platform.billing.domain.model.queries.GetOrganizationSubscriptionByOrganizationIdQuery;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Application-layer implementation of {@link SubscriptionBillingContextFacade}.
 *
 * @since 1.0
 */
@Service
public class SubscriptionBillingContextFacadeImpl implements SubscriptionBillingContextFacade {
    private final OrganizationSubscriptionCommandService organizationSubscriptionCommandService;
    private final OrganizationSubscriptionQueryService organizationSubscriptionQueryService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionBillingContextFacadeImpl(
            OrganizationSubscriptionCommandService organizationSubscriptionCommandService,
            OrganizationSubscriptionQueryService organizationSubscriptionQueryService,
            SubscriptionPlanRepository subscriptionPlanRepository
    ) {
        this.organizationSubscriptionCommandService = organizationSubscriptionCommandService;
        this.organizationSubscriptionQueryService = organizationSubscriptionQueryService;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeBaseSubscriptionForOrganization(Long organizationId) {
        organizationSubscriptionCommandService.handle(new InitializeBaseOrganizationSubscriptionCommand(organizationId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OrganizationEntitlementsSnapshot> fetchEntitlementsByOrganizationId(Long organizationId) {
        if (organizationId == null || organizationId <= 0) {
            return Optional.empty();
        }
        return organizationSubscriptionQueryService
                .handle(new GetOrganizationSubscriptionByOrganizationIdQuery(organizationId))
                .success()
                .map(details -> new OrganizationEntitlementsSnapshot(
                        details.subscription().getOrganizationId(),
                        details.subscription().getPlanCode(),
                        details.subscription().getStatus().name(),
                        details.entitlements().stream()
                                .map(this::toSnapshot)
                                .toList()
                ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<EntitlementCheckSnapshot> checkEntitlement(Long organizationId, String entitlementKey) {
        if (organizationId == null || organizationId <= 0 || entitlementKey == null || entitlementKey.isBlank()) {
            return Optional.empty();
        }

        var normalizedKey = entitlementKey.trim().toLowerCase(Locale.ROOT);
        var entitlements = fetchEntitlementsByOrganizationId(organizationId);
        if (entitlements.isEmpty()) {
            return Optional.of(unavailableEntitlement(
                    organizationId,
                    null,
                    null,
                    normalizedKey,
                    "Organization subscription is not configured"
            ));
        }

        var snapshot = entitlements.get();
        return Optional.of(snapshot.entitlements().stream()
                .filter(entitlement -> entitlement.key().equals(normalizedKey))
                .findFirst()
                .map(entitlement -> toCheckSnapshot(snapshot, entitlement))
                .orElseGet(() -> unavailableEntitlement(
                        organizationId,
                        snapshot.planCode(),
                        snapshot.status(),
                        normalizedKey,
                        "Entitlement is not configured for this operation"
                )));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUseEntitlement(Long organizationId, String entitlementKey) {
        return checkEntitlement(organizationId, entitlementKey)
                .map(EntitlementCheckSnapshot::enabled)
                .orElse(false);
    }

    private EntitlementItemSnapshot toSnapshot(OrganizationEntitlement entitlement) {
        return new EntitlementItemSnapshot(
                entitlement.key(),
                entitlement.category(),
                entitlement.enabled(),
                entitlement.limit(),
                entitlement.used(),
                entitlement.remaining(),
                entitlement.lockedReason()
        );
    }

    private EntitlementCheckSnapshot toCheckSnapshot(
            OrganizationEntitlementsSnapshot snapshot,
            EntitlementItemSnapshot entitlement
    ) {
        return new EntitlementCheckSnapshot(
                snapshot.organizationId(),
                snapshot.planCode(),
                snapshot.status(),
                entitlement.key(),
                entitlement.category(),
                entitlement.enabled(),
                entitlement.limit(),
                entitlement.used(),
                entitlement.remaining(),
                entitlement.lockedReason(),
                requiredPlanCodeFor(entitlement)
        );
    }

    private EntitlementCheckSnapshot unavailableEntitlement(
            Long organizationId,
            String planCode,
            String subscriptionStatus,
            String key,
            String lockedReason
    ) {
        return new EntitlementCheckSnapshot(
                organizationId,
                planCode,
                subscriptionStatus,
                key,
                null,
                false,
                null,
                null,
                null,
                lockedReason,
                requiredPlanCodeFor(key, null)
        );
    }

    private String requiredPlanCodeFor(EntitlementItemSnapshot entitlement) {
        if (Boolean.TRUE.equals(entitlement.enabled())) {
            return null;
        }
        return requiredPlanCodeFor(entitlement.key(), entitlement.used());
    }

    private String requiredPlanCodeFor(String entitlementKey, Integer used) {
        return subscriptionPlanRepository.findAllActive().stream()
                .filter(plan -> planAllowsEntitlement(plan, entitlementKey, used))
                .map(SubscriptionPlan::getCode)
                .findFirst()
                .orElse(null);
    }

    private boolean planAllowsEntitlement(SubscriptionPlan plan, String entitlementKey, Integer used) {
        return switch (entitlementKey) {
            case ENTITLEMENT_LOCATIONS -> capacityAllows(plan.getUsageLimits().maxLocations(), used);
            case ENTITLEMENT_ASSETS -> capacityAllows(plan.getUsageLimits().maxAssets(), used);
            case ENTITLEMENT_IOT_DEVICES -> capacityAllows(plan.getUsageLimits().maxIotDevices(), used);
            case ENTITLEMENT_USERS -> capacityAllows(plan.getUsageLimits().maxUsers(), used);
            case ENTITLEMENT_REPORT_HISTORY -> plan.getUsageLimits().historyRetentionDays() != null;
            case ENTITLEMENT_MAINTENANCE -> plan.getFeatureFlags().allowsMaintenance();
            case ENTITLEMENT_AI_GUIDANCE -> plan.getFeatureFlags().allowsAiGuidance();
            case ENTITLEMENT_AI_REPORT_SUMMARY -> plan.getFeatureFlags().allowsAiReportSummary();
            default -> false;
        };
    }

    private boolean capacityAllows(Integer limit, Integer used) {
        return limit == null || used == null || used < limit;
    }
}
