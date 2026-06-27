package com.acme.coldtrace.platform.billing.application.internal.queryservices;

import com.acme.coldtrace.platform.billing.application.queryservices.SubscriptionPlanQueryService;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.queries.GetActiveSubscriptionPlansQuery;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for subscription plan queries.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SubscriptionPlanQueryServiceImpl implements SubscriptionPlanQueryService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanQueryServiceImpl(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    /**
     * Retrieves active subscription plans.
     *
     * @param query active plans query
     * @return active plans
     */
    @Override
    public List<SubscriptionPlan> handle(GetActiveSubscriptionPlansQuery query) {
        log.debug("Querying active subscription plans");
        var plans = subscriptionPlanRepository.findAllActive();
        log.debug("Found {} active subscription plans", plans.size());
        return plans;
    }
}
