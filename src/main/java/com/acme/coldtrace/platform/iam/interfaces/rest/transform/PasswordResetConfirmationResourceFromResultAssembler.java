package com.acme.coldtrace.platform.iam.interfaces.rest.transform;

import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetConfirmationCommandResult;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.PasswordResetConfirmationResource;

/**
 * Interface layer translator converting password reset confirmation results to resources.
 *
 * @since 1.0
 */
public final class PasswordResetConfirmationResourceFromResultAssembler {
    private PasswordResetConfirmationResourceFromResultAssembler() {
    }

    public static PasswordResetConfirmationResource toResourceFromResult(
            PasswordResetConfirmationCommandResult result
    ) {
        return new PasswordResetConfirmationResource(result.changed());
    }
}
