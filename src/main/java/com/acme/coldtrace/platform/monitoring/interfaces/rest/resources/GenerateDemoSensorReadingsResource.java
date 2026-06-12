package com.acme.coldtrace.platform.monitoring.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

/**
 * Request resource used to generate demo sensor readings.
 *
 * @param assetId optional asset filter
 * @param count number of readings to generate
 * @since 1.0
 */
@Schema(
        name = "GenerateDemoSensorReadingsRequest",
        description = "Request payload for backend-owned demo telemetry generation"
)
public record GenerateDemoSensorReadingsResource(
        @Positive(message = "must be positive")
        @Schema(description = "Optional asset filter", example = "1")
        Long assetId,

        @Min(value = 1, message = "must be between 1 and 50")
        @Max(value = 50, message = "must be between 1 and 50")
        @Schema(description = "Number of demo readings to generate", example = "10")
        Integer count
) {
}
