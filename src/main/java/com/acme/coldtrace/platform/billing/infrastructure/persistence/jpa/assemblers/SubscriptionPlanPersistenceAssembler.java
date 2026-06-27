package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.PlanFeatureFlags;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.PlanUsageLimits;
import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities.SubscriptionPlanPersistenceEntity;

import java.util.ArrayList;

/**
 * Assembler that translates subscription plans between domain and persistence models.
 *
 * @since 1.0
 */
public final class SubscriptionPlanPersistenceAssembler {
    private SubscriptionPlanPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity
     * @return subscription plan aggregate
     */
    public static SubscriptionPlan toDomainFromPersistence(SubscriptionPlanPersistenceEntity entity) {
        return new SubscriptionPlan(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getCurrency(),
                entity.getMonthlyPriceCents(),
                entity.getStripePriceId(),
                entity.getRecommended(),
                entity.getRecommendedLabel(),
                entity.getActive(),
                new PlanUsageLimits(
                        entity.getMaxLocations(),
                        entity.getMaxAssets(),
                        entity.getMaxIotDevices(),
                        entity.getMaxUsers(),
                        entity.getHistoryRetentionDays()
                ),
                new PlanFeatureFlags(
                        entity.getAllowsExports(),
                        entity.getAllowsMaintenance(),
                        entity.getAllowsAiGuidance(),
                        entity.getAllowsAiReportSummary()
                ),
                entity.getIncludedFeatures()
        );
    }

    /**
     * Converts a domain aggregate into a new persistence entity.
     *
     * @param plan subscription plan aggregate
     * @return persistence entity
     */
    public static SubscriptionPlanPersistenceEntity toPersistenceFromDomain(SubscriptionPlan plan) {
        var entity = new SubscriptionPlanPersistenceEntity();
        entity.setId(plan.getId());
        copyDomainState(plan, entity);
        return entity;
    }

    /**
     * Copies domain state into an existing persistence entity.
     *
     * @param plan source aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(SubscriptionPlan plan, SubscriptionPlanPersistenceEntity entity) {
        entity.setCode(plan.getCode());
        entity.setName(plan.getDisplayName());
        entity.setDescription(plan.getDescription());
        entity.setCurrency(plan.getCurrency());
        entity.setMonthlyPriceCents(plan.getMonthlyPriceCents());
        entity.setStripePriceId(plan.getStripePriceId());
        entity.setRecommended(plan.getRecommended());
        entity.setRecommendedLabel(plan.getRecommendedLabel());
        entity.setActive(plan.getActive());
        entity.setMaxLocations(plan.getUsageLimits().maxLocations());
        entity.setMaxAssets(plan.getUsageLimits().maxAssets());
        entity.setMaxIotDevices(plan.getUsageLimits().maxIotDevices());
        entity.setMaxUsers(plan.getUsageLimits().maxUsers());
        entity.setHistoryRetentionDays(plan.getUsageLimits().historyRetentionDays());
        entity.setAllowsExports(plan.getFeatureFlags().allowsExports());
        entity.setAllowsMaintenance(plan.getFeatureFlags().allowsMaintenance());
        entity.setAllowsAiGuidance(plan.getFeatureFlags().allowsAiGuidance());
        entity.setAllowsAiReportSummary(plan.getFeatureFlags().allowsAiReportSummary());
        entity.setIncludedFeatures(new ArrayList<>(plan.getIncludedFeatures()));
    }
}
