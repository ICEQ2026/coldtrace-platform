package com.acme.coldtrace.platform.billing.application.queryservices;

import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionDetails;
import com.acme.coldtrace.platform.billing.domain.model.queries.GetOrganizationSubscriptionByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for organization subscription queries.
 *
 * @since 1.0
 */
public interface OrganizationSubscriptionQueryService {
    /**
     * Retrieves current subscription details for one organization.
     *
     * @param query query containing the organization identifier
     * @return subscription details or query failure
     */
    Result<OrganizationSubscriptionDetails, OrganizationSubscriptionQueryFailure> handle(
            GetOrganizationSubscriptionByOrganizationIdQuery query
    );
}
