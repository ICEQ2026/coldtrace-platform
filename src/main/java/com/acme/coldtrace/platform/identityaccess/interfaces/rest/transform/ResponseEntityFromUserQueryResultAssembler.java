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
     * Converts a list of users into a 200 HTTP response.
     *
     * @param result user query result
     * @param messageSource message source for localized failure details
     * @return response entity containing user resources
     */
    public static ResponseEntity<?> toResponseEntityFromList(
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

    private static HttpStatus statusFromFailure(UserQueryFailure failure) {
        if (failure instanceof UserQueryFailure.OrganizationNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private static String localizeMessage(MessageSource messageSource, UserQueryFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
