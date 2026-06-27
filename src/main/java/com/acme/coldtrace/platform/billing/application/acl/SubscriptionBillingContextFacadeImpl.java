package com.acme.coldtrace.platform.billing.application.acl;

import com.acme.coldtrace.platform.billing.application.commandservices.OrganizationSubscriptionCommandService;
import com.acme.coldtrace.platform.billing.application.model.OrganizationEntitlement;
import com.acme.coldtrace.platform.billing.application.queryservices.OrganizationSubscriptionQueryService;
import com.acme.coldtrace.platform.billing.domain.model.commands.InitializeBaseOrganizationSubscriptionCommand;
import com.acme.coldtrace.platform.billing.domain.model.queries.GetOrganizationSubscriptionByOrganizationIdQuery;
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

    public SubscriptionBillingContextFacadeImpl(
            OrganizationSubscriptionCommandService organizationSubscriptionCommandService,
            OrganizationSubscriptionQueryService organizationSubscriptionQueryService
    ) {
        this.organizationSubscriptionCommandService = organizationSubscriptionCommandService;
        this.organizationSubscriptionQueryService = organizationSubscriptionQueryService;
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
    public boolean canUseEntitlement(Long organizationId, String entitlementKey) {
        if (entitlementKey == null || entitlementKey.isBlank()) {
            return false;
        }
        var normalizedKey = entitlementKey.trim().toLowerCase(Locale.ROOT);
        return fetchEntitlementsByOrganizationId(organizationId)
                .stream()
                .flatMap(snapshot -> snapshot.entitlements().stream())
                .filter(entitlement -> entitlement.key().equals(normalizedKey))
                .findFirst()
                .map(EntitlementItemSnapshot::enabled)
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
}
