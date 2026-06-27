package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.model.OrganizationEntitlement;
import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionDetails;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.OrganizationEntitlementResource;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.OrganizationSubscriptionResource;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.OrganizationSubscriptionUsageResource;

/**
 * Assembler that converts organization subscription details into REST resources.
 *
 * @since 1.0
 */
public final class OrganizationSubscriptionResourceFromResultAssembler {
    private OrganizationSubscriptionResourceFromResultAssembler() {
    }

    /**
     * Converts application subscription details into a response resource.
     *
     * @param details organization subscription details
     * @return REST response resource
     */
    public static OrganizationSubscriptionResource toResourceFromResult(OrganizationSubscriptionDetails details) {
        var subscription = details.subscription();
        return new OrganizationSubscriptionResource(
                subscription.getId(),
                subscription.getOrganizationId(),
                subscription.getStatus().name(),
                subscription.getProvider().name(),
                subscription.getProviderCustomerId(),
                subscription.getProviderSubscriptionId(),
                subscription.getCurrentPeriodStart(),
                subscription.getCurrentPeriodEnd(),
                subscription.getCancelAtPeriodEnd(),
                subscription.getMetadata(),
                SubscriptionPlanResourceFromEntityAssembler.toResourceFromEntity(details.plan()),
                new OrganizationSubscriptionUsageResource(
                        details.usage().locations(),
                        details.usage().assets(),
                        details.usage().iotDevices(),
                        details.usage().users()
                ),
                details.entitlements().stream()
                        .map(OrganizationSubscriptionResourceFromResultAssembler::toResourceFromEntitlement)
                        .toList()
        );
    }

    private static OrganizationEntitlementResource toResourceFromEntitlement(OrganizationEntitlement entitlement) {
        return new OrganizationEntitlementResource(
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
