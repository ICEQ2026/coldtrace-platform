package com.acme.coldtrace.platform.aiassistance.domain.model.commands;

/**
 * Command for generating an advisory AI interpretation of the organization dashboard.
 *
 * @param organizationId organization identifier from the route
 * @param question optional operator question
 * @since 1.0
 */
public record GenerateDashboardAiInterpretationCommand(Long organizationId, String question) {
    private static final int QUESTION_MAX_LENGTH = 240;

    /**
     * Validates route and request values.
     *
     * @throws IllegalArgumentException when organization id or question is invalid
     */
    public GenerateDashboardAiInterpretationCommand {
        organizationId = requirePositive(
                organizationId,
                "ai-assistance.dashboard-interpretation.error.organizationId.invalid"
        );
        question = normalizeQuestion(question);
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static String normalizeQuestion(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        var normalized = value.trim();
        if (normalized.length() > QUESTION_MAX_LENGTH) {
            throw new IllegalArgumentException("ai-assistance.dashboard-interpretation.error.question.invalid");
        }
        return normalized;
    }
}
