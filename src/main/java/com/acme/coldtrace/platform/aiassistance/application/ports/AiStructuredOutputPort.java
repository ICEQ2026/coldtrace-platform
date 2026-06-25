package com.acme.coldtrace.platform.aiassistance.application.ports;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceFailure;
import com.acme.coldtrace.platform.aiassistance.application.model.AiGeneratedResponse;
import com.acme.coldtrace.platform.aiassistance.application.model.AiStructuredPrompt;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Port for requesting validated structured output from an AI provider.
 *
 * @since 1.0
 */
public interface AiStructuredOutputPort {
    /**
     * Requests structured output for the provided prompt and response type.
     *
     * @param prompt backend-owned prompt template
     * @param responseType structured output target type
     * @return generated response or controlled failure
     * @param <T> structured output type
     */
    <T> Result<AiGeneratedResponse<T>, AiAssistanceFailure> requestStructuredOutput(
            AiStructuredPrompt prompt,
            Class<T> responseType);
}
