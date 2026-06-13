package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

/**
 * Request resource used to create a preventive maintenance schedule.
 *
 * @param assetId asset that requires preventive maintenance
 * @param scheduledDate planned maintenance date and time
 * @param frequencyDays optional recurrence cadence in days
 * @param responsibleUserId optional responsible organization user identifier
 * @param observations optional planning observations
 * @param status initial maintenance schedule status
 * @since 1.0
 */
@Schema(
        name = "CreateMaintenanceScheduleRequest",
        description = "Request payload for scheduling preventive maintenance for an organization asset"
)
public record CreateMaintenanceScheduleResource(
        @NotNull(message = "is required")
        @Positive(message = "must be positive")
        @Schema(description = "Asset identifier owned by the organization", example = "1")
        Long assetId,

        @NotNull(message = "is required")
        @FutureOrPresent(message = "must be in the present or future")
        @Schema(description = "Planned maintenance date and time", example = "2026-06-20T09:00:00Z")
        OffsetDateTime scheduledDate,

        @Positive(message = "must be positive")
        @Schema(description = "Optional recurrence cadence in days", example = "30")
        Integer frequencyDays,

        @Positive(message = "must be positive")
        @Schema(description = "Optional responsible user identifier", example = "1")
        Long responsibleUserId,

        @Schema(description = "Optional planning observations", example = "Inspect compressor and thermal seals")
        String observations,

        @NotBlank(message = "is required")
        @Schema(description = "Initial lifecycle status", example = "scheduled")
        String status
) {
}
