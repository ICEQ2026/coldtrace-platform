package com.acme.coldtrace.platform.aiassistance.interfaces.rest.transform;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceFailure;
import com.acme.coldtrace.platform.aiassistance.application.commandservices.DashboardAiInterpretationCommandFailure;
import com.acme.coldtrace.platform.aiassistance.application.model.DashboardAiInterpretation;
import com.acme.coldtrace.platform.billing.interfaces.acl.PlanEntitlementFailure;
import com.acme.coldtrace.platform.billing.interfaces.acl.PlanEntitlementProblemProperties;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Interface layer translator converting dashboard AI command results to HTTP responses.
 *
 * @since 1.0
 */
public final class ResponseEntityFromDashboardAiInterpretationCommandResultAssembler {
    private ResponseEntityFromDashboardAiInterpretationCommandResultAssembler() {
    }

    /**
     * Converts a dashboard AI interpretation generation result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or controlled error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromGenerationResult(
            Result<DashboardAiInterpretation, DashboardAiInterpretationCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                interpretation -> ResponseEntity.ok(
                        DashboardAiInterpretationResourceFromResultAssembler.toResourceFromResult(interpretation)
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            DashboardAiInterpretationCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        var problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(
                        failure.messageKey(),
                        failure.args(),
                        failure.messageKey(),
                        LocaleContextHolder.getLocale()
                )
        );
        appendPlanEntitlementProperties(problemDetail, failure);
        return ResponseEntity.status(status).body(problemDetail);
    }

    private static HttpStatus statusFromFailure(DashboardAiInterpretationCommandFailure failure) {
        if (failure instanceof DashboardAiInterpretationCommandFailure.OrganizationNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        if (failure instanceof DashboardAiInterpretationCommandFailure.ProviderFailure providerFailure) {
            return statusFromAiFailure(providerFailure.failure());
        }
        if (failure instanceof DashboardAiInterpretationCommandFailure.PlanLimitExceeded) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static HttpStatus statusFromAiFailure(AiAssistanceFailure failure) {
        if (failure instanceof AiAssistanceFailure.ProviderTimeout) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        if (failure instanceof AiAssistanceFailure.InvalidStructuredOutput) {
            return HttpStatus.BAD_GATEWAY;
        }
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    private static void appendPlanEntitlementProperties(
            ProblemDetail problemDetail,
            DashboardAiInterpretationCommandFailure failure
    ) {
        if (failure instanceof PlanEntitlementFailure planFailure) {
            PlanEntitlementProblemProperties.from(planFailure.entitlement())
                    .forEach(problemDetail::setProperty);
        }
    }
}
