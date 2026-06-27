package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.SubscriptionPlanFeatureFlagsResource;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.SubscriptionPlanResource;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.SubscriptionPlanUsageLimitsResource;

/**
 * Assembler that converts subscription plan aggregates into REST resources.
 *
 * @since 1.0
 */
public class SubscriptionPlanResourceFromEntityAssembler {
    /**
     * Converts a subscription plan aggregate into a resource.
     *
     * @param entity subscription plan aggregate
     * @return subscription plan response resource
     */
    public static SubscriptionPlanResource toResourceFromEntity(SubscriptionPlan entity) {
        return new SubscriptionPlanResource(
                entity.getId(),
                entity.getCode(),
                entity.getDisplayName(),
                entity.getDescription(),
                entity.getMonthlyPriceCents(),
                entity.getCurrency(),
                entity.getStripePriceId(),
                entity.getRecommended(),
                entity.getRecommendedLabel(),
                entity.getActive(),
                new SubscriptionPlanUsageLimitsResource(
                        entity.getUsageLimits().maxLocations(),
                        entity.getUsageLimits().maxAssets(),
                        entity.getUsageLimits().maxIotDevices(),
                        entity.getUsageLimits().maxUsers(),
                        entity.getUsageLimits().historyRetentionDays()
                ),
                new SubscriptionPlanFeatureFlagsResource(
                        entity.getFeatureFlags().allowsExports(),
                        entity.getFeatureFlags().allowsMaintenance(),
                        entity.getFeatureFlags().allowsAiGuidance(),
                        entity.getFeatureFlags().allowsAiReportSummary()
                ),
                entity.getIncludedFeatures()
        );
    }
}
