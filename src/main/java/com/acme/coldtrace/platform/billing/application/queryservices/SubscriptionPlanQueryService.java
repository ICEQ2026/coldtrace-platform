package com.acme.coldtrace.platform.billing.application.queryservices;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.queries.GetActiveSubscriptionPlansQuery;

import java.util.List;

/**
 * Application service contract for subscription plan queries.
 *
 * @since 1.0
 */
public interface SubscriptionPlanQueryService {
    /**
     * Retrieves active subscription plans.
     *
     * @param query active plans query
     * @return active plans
     */
    List<SubscriptionPlan> handle(GetActiveSubscriptionPlansQuery query);
}
