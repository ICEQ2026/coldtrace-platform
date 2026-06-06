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

public class ResponseEntityFromUserCommandResultAssembler {
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

    private static HttpStatus statusFromFailure(UserCommandFailure failure) {
        if (failure instanceof UserCommandFailure.DuplicateEmail) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private static String localizeMessage(MessageSource messageSource, UserCommandFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}
