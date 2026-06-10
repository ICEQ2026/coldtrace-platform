package com.acme.coldtrace.platform.assetmanagement.domain.model.commands;

import java.util.List;

/**
 * Command for creating or updating asset settings.
 * <p>
 * The command supports both organization defaults and asset-specific settings.
 * When {@code assetId} is {@code null}, the command targets the organization
 * default. When {@code assetId} is present, the application service validates
 * that the asset belongs to the same organization before the aggregate is
 * created or updated.
 *
 * @param organizationId organization identifier that scopes the settings
 * @param assetId optional asset identifier for asset-specific settings
 * @param uuid optional external settings code
 * @param assetTypes asset types where these settings can apply
 * @param iotDeviceTypes IoT device types supported by these settings
 * @param minimumTemperature lower safe temperature
 * @param maximumTemperature upper safe temperature
 * @param minimumHumidity lower safe humidity
 * @param maximumHumidity upper safe humidity
 * @param calibrationFrequencyDays calibration cadence in days
 * @param temperatureUnit temperature unit label
 * @param humidityUnit humidity unit label
 * @param weightUnit weight unit label
 * @param readingFrequencySeconds telemetry cadence in seconds
 * @param alertThresholdMinutes alert delay threshold in minutes
 * @since 1.0
 */
public record SaveAssetSettingsCommand(
        Long organizationId,
        Long assetId,
        String uuid,
        List<String> assetTypes,
        List<String> iotDeviceTypes,
        Double minimumTemperature,
        Double maximumTemperature,
        Double minimumHumidity,
        Double maximumHumidity,
        Integer calibrationFrequencyDays,
        String temperatureUnit,
        String humidityUnit,
        String weightUnit,
        Integer readingFrequencySeconds,
        Integer alertThresholdMinutes
) {
    /**
     * Validates and normalizes settings data.
     *
     * @throws IllegalArgumentException if identifiers, ranges, lists or units are invalid
     */
    public SaveAssetSettingsCommand {
        organizationId = requirePositive(organizationId, "asset-management.asset-settings.error.organizationId.invalid");
        if (assetId != null) {
            assetId = requirePositive(assetId, "asset-management.asset-settings.error.assetId.invalid");
        }
        assetTypes = normalizeList(assetTypes);
        iotDeviceTypes = normalizeList(iotDeviceTypes);
        minimumTemperature = requireNumber(minimumTemperature, "asset-management.asset-settings.error.minimumTemperature.required");
        maximumTemperature = requireNumber(maximumTemperature, "asset-management.asset-settings.error.maximumTemperature.required");
        if (minimumTemperature >= maximumTemperature) {
            throw new IllegalArgumentException("asset-management.asset-settings.error.temperature-range.invalid");
        }
        minimumHumidity = minimumHumidity == null ? 0.0 : minimumHumidity;
        maximumHumidity = requireNumber(maximumHumidity, "asset-management.asset-settings.error.maximumHumidity.required");
        if (minimumHumidity < 0 || maximumHumidity > 100 || minimumHumidity >= maximumHumidity) {
            throw new IllegalArgumentException("asset-management.asset-settings.error.humidity-range.invalid");
        }
        calibrationFrequencyDays = requirePositive(
                calibrationFrequencyDays,
                "asset-management.asset-settings.error.calibrationFrequencyDays.invalid"
        );
        readingFrequencySeconds = readingFrequencySeconds == null
                ? 300
                : requirePositive(readingFrequencySeconds, "asset-management.asset-settings.error.readingFrequencySeconds.invalid");
        alertThresholdMinutes = alertThresholdMinutes == null
                ? 10
                : requirePositive(alertThresholdMinutes, "asset-management.asset-settings.error.alertThresholdMinutes.invalid");
        temperatureUnit = requireNonBlank(temperatureUnit, "asset-management.asset-settings.error.temperatureUnit.required");
        humidityUnit = requireNonBlank(humidityUnit, "asset-management.asset-settings.error.humidityUnit.required");
        weightUnit = requireNonBlank(weightUnit, "asset-management.asset-settings.error.weightUnit.required");
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Integer requirePositive(Integer value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Double requireNumber(Double value, String messageKey) {
        if (value == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static String requireNonBlank(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }

    private static List<String> normalizeList(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }
}
