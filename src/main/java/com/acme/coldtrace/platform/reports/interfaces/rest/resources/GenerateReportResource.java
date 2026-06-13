package com.acme.coldtrace.platform.reports.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * Request resource used to generate a report.
 *
 * @param type report type
 * @param title human-readable title
 * @param periodStart inclusive lower data bound
 * @param periodEnd inclusive upper data bound
 * @since 1.0
 */
@Schema(
        name = "GenerateReportRequest",
        description = "Request payload for generating a backend-owned operational report"
)
public record GenerateReportResource(
        @NotBlank(message = "is required")
        @Schema(description = "Report type", example = "COMPLIANCE")
        String type,

        @NotBlank(message = "is required")
        @Schema(description = "Human-readable report title", example = "June compliance summary")
        String title,

        @NotNull(message = "is required")
        @Schema(description = "Inclusive lower date-time bound", example = "2026-06-01T00:00:00Z")
        OffsetDateTime periodStart,

        @NotNull(message = "is required")
        @Schema(description = "Inclusive upper date-time bound", example = "2026-06-30T23:59:59Z")
        OffsetDateTime periodEnd
) {
}
