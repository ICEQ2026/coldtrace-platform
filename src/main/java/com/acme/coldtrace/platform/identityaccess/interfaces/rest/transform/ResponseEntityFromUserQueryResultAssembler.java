package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.application.queryservices.UserQueryFailure;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting user query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromUserQueryResultAssembler {
    /**
     * Converts a user query result into an HTTP response.
     *
     * @param result user query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<List<User>, UserQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                users -> ResponseEntity.ok(users.stream()
                        .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizeMessage(messageSource, failure)
                    ));
                }
        );
    }

    /**
     * Maps a user query failure to an HTTP status.
     *
     * @param failure user query failure
     * @return HTTP status for the failure
     */
    private static HttpStatus statusFromFailure(UserQueryFailure failure) {
        if (failure instanceof UserQueryFailure.OrganizationNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Resolves the localized message for a user query failure.
     *
     * @param messageSource message source for i18n
     * @param failure user query failure
     * @return localized message or message key fallback
     */
    private static String localizeMessage(MessageSource messageSource, UserQueryFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
