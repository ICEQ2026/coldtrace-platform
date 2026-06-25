package com.acme.coldtrace.platform.aiassistance.interfaces.rest.transform;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceFailure;
import com.acme.coldtrace.platform.aiassistance.application.model.AiGeneratedResponse;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

/**
 * Interface-layer translator for AI assistance command results.
 *
 * @since 1.0
 */
public final class ResponseEntityFromAiAssistanceResultAssembler {
    private ResponseEntityFromAiAssistanceResultAssembler() {
    }

    /**
     * Converts an AI generation result into an HTTP response.
     *
     * @param result AI generation result
     * @param successMapper mapper from application output to REST resource
     * @param messageSource message source for localized failure details
     * @return success response or controlled provider error response
     * @param <T> structured output type
     * @param <R> REST resource type
     */
    public static <T, R> ResponseEntity<?> toResponseEntityFromGenerationResult(
            Result<AiGeneratedResponse<T>, AiAssistanceFailure> result,
            Function<AiGeneratedResponse<T>, R> successMapper,
            MessageSource messageSource
    ) {
        return result.fold(
                response -> ResponseEntity.ok(successMapper.apply(response)),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            AiAssistanceFailure failure,
            MessageSource messageSource) {
        var status = toStatusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(
                        failure.messageKey(),
                        failure.args(),
                        failure.messageKey(),
                        LocaleContextHolder.getLocale()
                )
        ));
    }

    private static HttpStatus toStatusFromFailure(AiAssistanceFailure failure) {
        if (failure instanceof AiAssistanceFailure.ProviderTimeout) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        if (failure instanceof AiAssistanceFailure.InvalidStructuredOutput) {
            return HttpStatus.BAD_GATEWAY;
        }
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
