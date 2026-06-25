package com.acme.coldtrace.platform.aiassistance.application.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * One structured insight for dashboard interpretation.
 *
 * @param title short insight title
 * @param metric metric or operational area referenced by the insight
 * @param interpretation model interpretation
 * @param severity business severity for operators
 * @since 1.0
 */
@JsonPropertyOrder({"title", "metric", "interpretation", "severity"})
public record DashboardInsightDraft(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 100) String metric,
        @NotBlank @Size(max = 360) String interpretation,
        @NotBlank @Size(max = 80) String severity
) {
}
