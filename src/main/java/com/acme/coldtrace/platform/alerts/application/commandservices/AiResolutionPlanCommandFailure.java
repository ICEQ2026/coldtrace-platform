package com.acme.coldtrace.platform.alerts.application.commandservices;

/**
 * Failure types for AI resolution plan command execution.
 *
 * @since 1.0
 */
public sealed interface AiResolutionPlanCommandFailure
        permits AiResolutionPlanCommandFailure.OrganizationNotFound,
        AiResolutionPlanCommandFailure.IncidentNotFound,
        AiResolutionPlanCommandFailure.PlanNotFound,
        AiResolutionPlanCommandFailure.PlanAlreadyDecided {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements AiResolutionPlanCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.ai-resolution-plan.error.organization-not-found";
        }
    }

    /** Incident not found failure. */
    record IncidentNotFound() implements AiResolutionPlanCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.ai-resolution-plan.error.incident-not-found";
        }
    }

    /** AI resolution plan not found failure. */
    record PlanNotFound() implements AiResolutionPlanCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.ai-resolution-plan.error.plan-not-found";
        }
    }

    /** AI resolution plan has already been approved or rejected. */
    record PlanAlreadyDecided() implements AiResolutionPlanCommandFailure {
        @Override
        public String messageKey() {
            return "alerts.ai-resolution-plan.error.plan-already-decided";
        }
    }
}
