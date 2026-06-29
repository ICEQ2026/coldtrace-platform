package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.domain.model.commands.CreateBillingPortalSessionCommand;

/**
 * Converts portal session route parameters into command objects.
 *
 * @since 1.0
 */
public final class CreateBillingPortalSessionCommandFromResourceAssembler {
    private CreateBillingPortalSessionCommandFromResourceAssembler() {
    }

    /**
     * Creates a portal session command from the organization route parameter.
     *
     * @param organizationId organization identifier
     * @return portal session command
     */
    public static CreateBillingPortalSessionCommand toCommandFromResource(Long organizationId) {
        return new CreateBillingPortalSessionCommand(organizationId);
    }
}
