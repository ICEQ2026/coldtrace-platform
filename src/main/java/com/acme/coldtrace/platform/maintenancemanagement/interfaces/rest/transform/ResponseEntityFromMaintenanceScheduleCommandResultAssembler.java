package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.MaintenanceScheduleCommandFailure;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.billing.interfaces.acl.PlanEntitlementFailure;
import com.acme.coldtrace.platform.billing.interfaces.acl.PlanEntitlementProblemProperties;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting maintenance schedule command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromMaintenanceScheduleCommandResultAssembler {
    /**
     * Converts a schedule creation result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromCreateResult(
            Result<MaintenanceSchedule, MaintenanceScheduleCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                schedule -> new ResponseEntity<>(
                        MaintenanceScheduleResourceFromEntityAssembler.toResourceFromEntity(schedule),
                        CREATED
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a schedule status update result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromStatusUpdateResult(
            Result<MaintenanceSchedule, MaintenanceScheduleCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                schedule -> ResponseEntity.ok(
                        MaintenanceScheduleResourceFromEntityAssembler.toResourceFromEntity(schedule)
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            MaintenanceScheduleCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        var problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure.messageKey(), failure.args())
        );
        appendPlanEntitlementProperties(problemDetail, failure);
        return ResponseEntity.status(status).body(problemDetail);
    }

    private static HttpStatus statusFromFailure(MaintenanceScheduleCommandFailure failure) {
        if (failure instanceof MaintenanceScheduleCommandFailure.OrganizationNotFound ||
                failure instanceof MaintenanceScheduleCommandFailure.AssetNotFound ||
                failure instanceof MaintenanceScheduleCommandFailure.ResponsibleUserNotFound ||
                failure instanceof MaintenanceScheduleCommandFailure.MaintenanceScheduleNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        if (failure instanceof MaintenanceScheduleCommandFailure.DuplicateActiveSchedule ||
                failure instanceof MaintenanceScheduleCommandFailure.InvalidStatusTransition ||
                failure instanceof MaintenanceScheduleCommandFailure.PlanLimitExceeded) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private static String localizeMessage(MessageSource messageSource, String messageKey, Object[] args) {
        return messageSource.getMessage(
                messageKey,
                args,
                messageKey,
                LocaleContextHolder.getLocale()
        );
    }

    private static void appendPlanEntitlementProperties(
            ProblemDetail problemDetail,
            MaintenanceScheduleCommandFailure failure
    ) {
        if (failure instanceof PlanEntitlementFailure planFailure) {
            PlanEntitlementProblemProperties.from(planFailure.entitlement())
                    .forEach(problemDetail::setProperty);
        }
    }
}
