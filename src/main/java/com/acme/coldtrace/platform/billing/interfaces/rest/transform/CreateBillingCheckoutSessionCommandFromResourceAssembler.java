package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.domain.model.commands.CreateBillingCheckoutSessionCommand;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.CreateBillingCheckoutSessionResource;

/**
 * Converts checkout session REST resources into commands.
 *
 * @since 1.0
 */
public final class CreateBillingCheckoutSessionCommandFromResourceAssembler {
    private CreateBillingCheckoutSessionCommandFromResourceAssembler() {
    }

    /**
     * Converts the resource into a command.
     *
     * @param organizationId organization identifier
     * @param resource checkout session request resource
     * @return checkout session command
     */
    public static CreateBillingCheckoutSessionCommand toCommandFromResource(
            Long organizationId,
            CreateBillingCheckoutSessionResource resource
    ) {
        return new CreateBillingCheckoutSessionCommand(organizationId, resource.targetPlanCode());
    }
}
