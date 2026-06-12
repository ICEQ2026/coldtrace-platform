package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.application.queryservices.MaintenanceScheduleQueryFailure;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting maintenance schedule query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromMaintenanceScheduleQueryResultAssembler {
    /**
     * Converts a schedule list query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromListResult(
            Result<List<MaintenanceSchedule>, MaintenanceScheduleQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                schedules -> ResponseEntity.ok(schedules.stream()
                        .map(MaintenanceScheduleResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a single schedule query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromScheduleResult(
            Result<MaintenanceSchedule, MaintenanceScheduleQueryFailure> result,
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
            MaintenanceScheduleQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = HttpStatus.NOT_FOUND;
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
}
