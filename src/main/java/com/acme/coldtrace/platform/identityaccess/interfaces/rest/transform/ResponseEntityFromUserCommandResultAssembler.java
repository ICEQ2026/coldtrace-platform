package com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform;

import com.acme.coldtrace.platform.identityaccess.application.commandservices.UserCommandFailure;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting user command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromUserCommandResultAssembler {
    /**
     * Converts a user command result into an HTTP response.
     *
     * @param result user command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<User, UserCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                user -> new ResponseEntity<>(UserResourceFromEntityAssembler.toResourceFromEntity(user), CREATED),
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
     * Maps a user command failure to an HTTP status.
     *
     * @param failure user command failure
     * @return HTTP status for the failure
     */
    private static HttpStatus statusFromFailure(UserCommandFailure failure) {
        if (failure instanceof UserCommandFailure.DuplicateEmail) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Resolves the localized message for a user command failure.
     *
     * @param messageSource message source for i18n
     * @param failure user command failure
     * @return localized message or message key fallback
     */
    private static String localizeMessage(MessageSource messageSource, UserCommandFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
