package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.domain.model.commands.ConfirmPasswordResetCommand;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.ConfirmPasswordResetResource;

/**
 * Interface layer translator converting password reset confirmation resources to commands.
 *
 * @since 1.0
 */
public final class ConfirmPasswordResetCommandFromResourceAssembler {
    private ConfirmPasswordResetCommandFromResourceAssembler() {
    }

    public static ConfirmPasswordResetCommand toCommandFromResource(
            ConfirmPasswordResetResource resource
    ) {
        return new ConfirmPasswordResetCommand(resource.token(), resource.password());
    }
}
