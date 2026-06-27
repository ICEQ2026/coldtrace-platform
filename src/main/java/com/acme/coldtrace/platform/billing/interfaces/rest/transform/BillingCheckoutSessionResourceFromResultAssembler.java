package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.model.BillingCheckoutSession;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.BillingCheckoutSessionResource;

/**
 * Converts checkout session application results into REST resources.
 *
 * @since 1.0
 */
public final class BillingCheckoutSessionResourceFromResultAssembler {
    private BillingCheckoutSessionResourceFromResultAssembler() {
    }

    /**
     * Converts an application checkout session into a response resource.
     *
     * @param session checkout session
     * @return checkout session response resource
     */
    public static BillingCheckoutSessionResource toResourceFromResult(BillingCheckoutSession session) {
        return new BillingCheckoutSessionResource(
                session.provider(),
                session.sessionId(),
                session.checkoutUrl(),
                session.targetPlanCode()
        );
    }
}
