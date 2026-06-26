package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting subscription plan query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromSubscriptionPlanQueryResultAssembler {
    /**
     * Converts a list of subscription plans into a 200 HTTP response.
     *
     * @param plans subscription plans
     * @return response entity containing plan resources
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<SubscriptionPlan> plans) {
        return ResponseEntity.ok(plans.stream()
                .map(SubscriptionPlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
