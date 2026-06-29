package com.acme.coldtrace.platform.billing.interfaces.rest;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingWebhookCommandService;
import com.acme.coldtrace.platform.billing.domain.model.commands.ProcessStripeWebhookCommand;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.BillingWebhookProcessingResource;
import com.acme.coldtrace.platform.billing.interfaces.rest.transform.ResponseEntityFromBillingWebhookCommandResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller receiving signed Stripe billing webhooks.
 *
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/billing/stripe/webhooks", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Billing Stripe Webhooks", description = "Endpoint for signed Stripe billing webhook synchronization")
public class BillingStripeWebhooksController {
    private static final String STRIPE_SIGNATURE_HEADER = "Stripe-Signature";

    private final BillingWebhookCommandService billingWebhookCommandService;
    private final MessageSource messageSource;

    public BillingStripeWebhooksController(
            BillingWebhookCommandService billingWebhookCommandService,
            MessageSource messageSource
    ) {
        this.billingWebhookCommandService = billingWebhookCommandService;
        this.messageSource = messageSource;
    }

    /**
     * Processes a signed Stripe webhook.
     *
     * @param payload raw request body
     * @param signatureHeader Stripe-Signature header
     * @return processing result or controlled failure response
     */
    @Operation(
            summary = "Process Stripe billing webhook",
            description = "Verifies the Stripe signature and synchronizes local organization subscription state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook event processed or safely ignored",
                    content = @Content(schema = @Schema(implementation = BillingWebhookProcessingResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing signature, invalid signature, or invalid payload",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "503", description = "Stripe webhook signing secret is not configured",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(name = STRIPE_SIGNATURE_HEADER, required = false) String signatureHeader
    ) {
        log.debug("POST /api/v1/billing/stripe/webhooks");
        var result = billingWebhookCommandService.handle(new ProcessStripeWebhookCommand(
                payload,
                signatureHeader
        ));
        return ResponseEntityFromBillingWebhookCommandResultAssembler.toResponseEntityFromResult(
                result,
                messageSource
        );
    }
}
