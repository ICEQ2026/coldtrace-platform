package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.application.queryservices.NotificationQueryFailure;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Notification;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting notification query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromNotificationQueryResultAssembler {
    /**
     * Converts a notification list query result into an HTTP response.
     *
     * @param result notification list query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromListResult(
            Result<List<Notification>, NotificationQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                notifications -> ResponseEntity.ok(notifications.stream()
                        .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            NotificationQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure.messageKey(), failure.args())
        ));
    }

    private static String localizeMessage(MessageSource messageSource, String messageKey, Object[] args) {
        return messageSource.getMessage(
                messageKey,
                args,
                messageKey,
                LocaleContextHolder.getLocale()
        );
    }
}
