package com.acme.coldtrace.platform.iam.interfaces.rest;

import com.acme.coldtrace.platform.iam.application.commandservices.UserCommandService;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.resources.SignInResource;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.acme.coldtrace.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
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
 * REST controller exposing authentication endpoints.
 *
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthenticationController {
    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    /**
     * Authenticates a user and returns authenticated user data with JWT token.
     *
     * @param resource sign-in request resource
     * @return authenticated user resource or standardized error response
     */
    @Operation(
            summary = "User sign-in",
            description = "Authenticates an organization user with email and password, then returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserResource.class))),
            @ApiResponse(responseCode = "400", description = "Malformed request",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResource.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource resource) {
        log.debug("POST /api/v1/authentication/sign-in - email={}", resource.email());
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = userCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                authenticated -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticated.user(),
                        authenticated.token()
                ),
                HttpStatus.OK
        );
    }
}
