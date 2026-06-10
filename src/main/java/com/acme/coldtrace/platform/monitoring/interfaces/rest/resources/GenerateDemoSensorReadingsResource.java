package com.acme.coldtrace.platform.monitoring.interfaces.rest.resources;

/**
 * Request resource used to generate demo sensor readings.
 *
 * @param assetId optional asset filter
 * @param count number of readings to generate
 * @since 1.0
 */
public record GenerateDemoSensorReadingsResource(Long assetId, Integer count) {
}
