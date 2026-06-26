package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for rejecting a pending AI resolution plan.
 *
 * @since 1.0
 */
public record RejectAiResolutionPlanCommand(
        Long organizationId,
        Long incidentId,
        Long planId,
        String rejectedBy,
        String rejectionReason
) {
}
