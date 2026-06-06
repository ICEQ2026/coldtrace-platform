package com.acme.coldtrace.platform.identityaccess.interfaces.rest;

import com.acme.coldtrace.platform.identityaccess.application.commandservices.UserCommandService;
import com.acme.coldtrace.platform.identityaccess.application.queryservices.UserQueryService;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllUsersQuery;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.CreateUserResource;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.resources.UserResource;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform.CreateUserCommandFromResourceAssembler;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform.ResponseEntityFromUserCommandResultAssembler;
import com.acme.coldtrace.platform.identityaccess.interfaces.rest.transform.ResponseEntityFromUserQueryResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing user endpoints.
 * It translates HTTP requests into user commands and queries for identity access.
 *
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints for users")
public class UsersController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final MessageSource messageSource;

    public UsersController(
            UserCommandService userCommandService,
            UserQueryService userQueryService,
            MessageSource messageSource
    ) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.messageSource = messageSource;
    }

    /**
     * Gets all users.
     *
     * @return response entity containing user resources
     */
    @Operation(summary = "Get all users", description = "Gets all registered users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResource.class))))
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.debug("GET /users");
        var users = userQueryService.handle(new GetAllUsersQuery());
        return ResponseEntityFromUserQueryResultAssembler.toResponseEntityFromList(users);
    }

    /**
     * Creates a user.
     *
     * @param resource user creation request resource
     * @return response entity containing the created user resource or failure detail
     */
    @Operation(
            summary = "Create a user",
            description = "Creates a user linked to an organization and role",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User creation request",
                    content = @Content(schema = @Schema(implementation = CreateUserResource.class))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - email already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserResource resource) {
        log.debug("POST /users - email={}, organizationId={}, roleId={}",
                resource.email(), resource.organizationId(), resource.roleId());
        var command = CreateUserCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.handle(command);
        return ResponseEntityFromUserCommandResultAssembler.toResponseEntityFromResult(user, messageSource);
    }
}
