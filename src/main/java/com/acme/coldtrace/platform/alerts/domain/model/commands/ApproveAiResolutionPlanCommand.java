package com.acme.coldtrace.platform.alerts.domain.model.commands;

/**
 * Command for approving a pending AI resolution plan.
 *
 * @since 1.0
 */
public record ApproveAiResolutionPlanCommand(
        Long organizationId,
        Long incidentId,
        Long planId,
        String approvedBy,
        String finalCorrectiveAction,
        String finalResolutionNotes
) {
}
