package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources;

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
public record SaveAssetSettingsResource(
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
