package com.acme.coldtrace.platform.aiassistance.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Request resource for dashboard AI interpretation generation.
 *
 * @param question optional operator question
 * @since 1.0
 */
@Schema(description = "Request payload for dashboard AI interpretation")
public record GenerateDashboardAiInterpretationResource(
        @Schema(description = "Optional dashboard question", example = "What should I review first?")
        @Size(max = 240)
        String question
) {
}
