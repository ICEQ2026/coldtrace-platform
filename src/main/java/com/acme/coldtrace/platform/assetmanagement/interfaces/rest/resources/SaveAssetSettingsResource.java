package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Request resource used to create or update asset settings.
 *
 * @param uuid optional settings code
 * @param assetTypes supported asset types
 * @param iotDeviceTypes supported IoT device types
 * @param minimumTemperature lower safe temperature
 * @param maximumTemperature upper safe temperature
 * @param minimumHumidity optional lower safe humidity; defaults to 0 when omitted
 * @param maximumHumidity upper safe humidity
 * @param calibrationFrequencyDays calibration cadence in days
 * @param temperatureUnit temperature unit label
 * @param humidityUnit humidity unit label
 * @param weightUnit weight unit label
 * @param readingFrequencySeconds optional telemetry cadence in seconds
 * @param alertThresholdMinutes optional alert delay threshold in minutes
 * @since 1.0
 */
@Schema(
        name = "SaveAssetSettingsRequest",
        description = "Request payload for creating or updating safety thresholds and operational settings"
)
public record SaveAssetSettingsResource(
        @Schema(description = "Optional settings code", example = "DEFAULT-COLD-CHAIN")
        String uuid,

        @Schema(description = "Asset types covered by these settings", example = "[\"FREEZER\", \"COLD_ROOM\"]")
        List<String> assetTypes,

        @Schema(description = "IoT device types covered by these settings", example = "[\"TEMPERATURE_SENSOR\"]")
        List<String> iotDeviceTypes,

        @NotNull(message = "is required")
        @Schema(description = "Lower safe temperature threshold", example = "2.0")
        Double minimumTemperature,

        @NotNull(message = "is required")
        @Schema(description = "Upper safe temperature threshold", example = "8.0")
        Double maximumTemperature,

        @Schema(description = "Lower safe humidity threshold", example = "30.0")
        Double minimumHumidity,

        @NotNull(message = "is required")
        @Schema(description = "Upper safe humidity threshold", example = "65.0")
        Double maximumHumidity,

        @Positive(message = "must be positive")
        @Schema(description = "Calibration frequency in days", example = "30")
        Integer calibrationFrequencyDays,

        @NotBlank(message = "is required")
        @Schema(description = "Temperature unit", example = "C")
        String temperatureUnit,

        @NotBlank(message = "is required")
        @Schema(description = "Humidity unit", example = "%")
        String humidityUnit,

        @NotBlank(message = "is required")
        @Schema(description = "Weight unit", example = "kg")
        String weightUnit,

        @Positive(message = "must be positive")
        @Schema(description = "Telemetry reading frequency in seconds", example = "300")
        Integer readingFrequencySeconds,

        @Positive(message = "must be positive")
        @Schema(description = "Delay threshold before alert escalation in minutes", example = "15")
        Integer alertThresholdMinutes
) {
}
