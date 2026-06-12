package com.acme.coldtrace.platform.assetmanagement.domain.model.commands;

import java.time.LocalDate;
import java.util.List;

/**
 * Command for creating an IoT device.
 * <p>
 * IoT devices are registered under an organization and connected to one gateway.
 * They can optionally be assigned to an asset, but assignment compatibility is
 * validated by the application service because it requires loading both gateway
 * and asset aggregates.
 *
 * @param organizationId organization identifier from the route
 * @param gatewayId gateway where the device is connected
 * @param uuid device business identifier
 * @param deviceType supported device type, for example temperature-sensor or multi-sensor
 * @param model manufacturer or model label shown to operators
 * @param measurementType human-readable measurement type label
 * @param measurementParameters normalized parameters produced by the device
 * @param readingFrequencySeconds expected reading cadence in seconds
 * @param assetId optional assigned asset identifier
 * @param status operational status of the device
 * @param calibrationStatus calibration status shown in operational workflows
 * @param lastCalibrationDate last calibration date
 * @param nextCalibrationDate next expected calibration date
 * @since 1.0
 */
public record CreateIoTDeviceCommand(
        Long organizationId,
        Long gatewayId,
        String uuid,
        String deviceType,
        String model,
        String measurementType,
        List<String> measurementParameters,
        Integer readingFrequencySeconds,
        Long assetId,
        String status,
        String calibrationStatus,
        LocalDate lastCalibrationDate,
        LocalDate nextCalibrationDate
) {
    /**
     * Validates and normalizes IoT device creation data.
     *
     * @throws IllegalArgumentException if required values are missing or identifiers are invalid
     */
    public CreateIoTDeviceCommand {
        organizationId = requirePositive(organizationId, "asset-management.iot-device.error.organizationId.invalid");
        gatewayId = requirePositive(gatewayId, "asset-management.iot-device.error.gatewayId.invalid");
        assetId = requireOptionalPositive(assetId, "asset-management.iot-device.error.assetId.invalid");
        uuid = requireNonBlank(uuid, "asset-management.iot-device.error.uuid.required");
        deviceType = requireNonBlank(deviceType, "asset-management.iot-device.error.deviceType.required");
        model = requireNonBlank(model, "asset-management.iot-device.error.model.required");
        measurementType = requireNonBlank(measurementType, "asset-management.iot-device.error.measurementType.required");
        measurementParameters = normalizeParameters(measurementParameters, measurementType);
        readingFrequencySeconds = requirePositiveInteger(
                readingFrequencySeconds == null ? 3600 : readingFrequencySeconds,
                "asset-management.iot-device.error.readingFrequencySeconds.invalid"
        );
        status = requireNonBlank(status, "asset-management.iot-device.error.status.required").toLowerCase();
        calibrationStatus = requireNonBlank(
                calibrationStatus,
                "asset-management.iot-device.error.calibrationStatus.required"
        ).toLowerCase();
        lastCalibrationDate = requireDate(lastCalibrationDate, "asset-management.iot-device.error.lastCalibrationDate.required");
        nextCalibrationDate = requireDate(nextCalibrationDate, "asset-management.iot-device.error.nextCalibrationDate.required");
    }

    private static Long requirePositive(Long value, String messageKey) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Long requireOptionalPositive(Long value, String messageKey) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static Integer requirePositiveInteger(Integer value, String messageKey) {
        if (value == null || value <= 0) {
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

    private static LocalDate requireDate(LocalDate value, String messageKey) {
        if (value == null) {
            throw new IllegalArgumentException(messageKey);
        }
        return value;
    }

    private static List<String> normalizeParameters(List<String> parameters, String measurementType) {
        var normalized = parameters == null ? List.<String>of() : parameters.stream()
                .filter(parameter -> parameter != null && !parameter.isBlank())
                .map(parameter -> parameter.trim().toLowerCase())
                .distinct()
                .toList();
        if (!normalized.isEmpty()) {
            return normalized;
        }
        return List.of(measurementType.trim().toLowerCase().replace(" ", "-"));
    }
}
