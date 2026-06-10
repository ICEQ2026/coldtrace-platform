package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

import java.util.List;

/**
 * Response resource representing asset settings.
 *
 * @param id settings identifier
 * @param organizationId organization identifier
 * @param assetId optional asset identifier for asset-specific settings
 * @param uuid generated settings code
 * @param assetTypes supported asset types
 * @param iotDeviceTypes supported IoT device types
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
public record AssetSettingsResource(
        Long id,
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
}
