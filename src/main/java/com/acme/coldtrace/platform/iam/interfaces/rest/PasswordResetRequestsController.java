package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.application.commandservices.PasswordResetRequestCommandService;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.ConfirmPasswordResetResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.CreatePasswordResetRequestResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.PasswordResetConfirmationResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.PasswordResetRequestResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.ConfirmPasswordResetCommandFromResourceAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.CreatePasswordResetRequestCommandFromResourceAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.PasswordResetConfirmationResourceFromResultAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.PasswordResetRequestResourceFromResultAssembler;
import com.acme.coldtrace.platform.shared.interfaces.rest.resources.ErrorResource;
import com.acme.coldtrace.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing password reset request endpoints.
 *
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/password-reset-requests", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Password Reset Requests", description = "Password recovery request and confirmation endpoints")
public class PasswordResetRequestsController {
    private final PasswordResetRequestCommandService passwordResetRequestCommandService;

    public PasswordResetRequestsController(PasswordResetRequestCommandService passwordResetRequestCommandService) {
        this.passwordResetRequestCommandService = passwordResetRequestCommandService;
    }

    /**
     * Accepts a password reset request without revealing whether the email exists.
     *
     * @param resource password reset request payload
     * @return generic accepted response or standardized error response
     */
    @Operation(
            summary = "Request password reset",
            description = "Accepts a password reset request and records safe token metadata when the submitted email belongs to a ColdTrace user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Password reset request accepted",
                    content = @Content(schema = @Schema(implementation = PasswordResetRequestResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "500", description = "Password reset request could not be prepared",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPasswordResetRequest(
            @Valid @RequestBody CreatePasswordResetRequestResource resource
    ) {
        log.debug("POST /api/v1/password-reset-requests");
        var command = CreatePasswordResetRequestCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = passwordResetRequestCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PasswordResetRequestResourceFromResultAssembler::toResourceFromResult,
                HttpStatus.ACCEPTED
        );
    }

    /**
     * Confirms a password reset token and changes the user password.
     *
     * @param resource password reset confirmation payload
     * @return confirmation response or standardized error response
     */
    @Operation(
            summary = "Confirm password reset",
            description = "Consumes a one-time password reset token received by email and updates the user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset confirmed",
                    content = @Content(schema = @Schema(implementation = PasswordResetConfirmationResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "422", description = "Invalid or expired reset token",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "500", description = "Password reset confirmation could not be prepared",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class)))
    })
    @PostMapping(value = "/confirmations", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmPasswordReset(
            @Valid @RequestBody ConfirmPasswordResetResource resource
    ) {
        log.debug("POST /api/v1/password-reset-requests/confirmations");
        var command = ConfirmPasswordResetCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = passwordResetRequestCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PasswordResetConfirmationResourceFromResultAssembler::toResourceFromResult,
                HttpStatus.OK
        );
    }
}
