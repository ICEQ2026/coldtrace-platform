package com.acme.coldtrace.platform.aiassistance.application.commandservices;

/**
 * Failure types for dashboard AI interpretation generation.
 *
 * @since 1.0
 */
public sealed interface DashboardAiInterpretationCommandFailure
        permits DashboardAiInterpretationCommandFailure.OrganizationNotFound,
        DashboardAiInterpretationCommandFailure.ContextAssemblyFailed,
        DashboardAiInterpretationCommandFailure.ProviderFailure {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements DashboardAiInterpretationCommandFailure {
        @Override
        public String messageKey() {
            return "ai-assistance.dashboard-interpretation.error.organization-not-found";
        }
    }

    /** Backend dashboard context could not be serialized for the AI prompt. */
    record ContextAssemblyFailed() implements DashboardAiInterpretationCommandFailure {
        @Override
        public String messageKey() {
            return "ai-assistance.dashboard-interpretation.error.context-unavailable";
        }
    }

    /** AI provider failure while generating structured dashboard interpretation. */
    record ProviderFailure(AiAssistanceFailure failure) implements DashboardAiInterpretationCommandFailure {
        @Override
        public String messageKey() {
            return failure.messageKey();
        }

        @Override
        public Object[] args() {
            return failure.args();
        }
    }
}
