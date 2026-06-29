package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.model.BillingPortalSession;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.BillingPortalSessionResource;

/**
 * Converts customer portal session application results into REST resources.
 *
 * @since 1.0
 */
public final class BillingPortalSessionResourceFromResultAssembler {
    private BillingPortalSessionResourceFromResultAssembler() {
    }

    /**
     * Converts an application customer portal session into a response resource.
     *
     * @param session portal session
     * @return portal session response resource
     */
    public static BillingPortalSessionResource toResourceFromResult(BillingPortalSession session) {
        return new BillingPortalSessionResource(
                session.provider(),
                session.sessionId(),
                session.portalUrl(),
                session.organizationId()
        );
    }
}
