package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetIoTDeviceByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetIoTDevicesByOrganizationIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for IoT device query operations.
 * <p>
 * The interface layer uses this service to retrieve organization-scoped device
 * data without depending on persistence details.
 *
 * @since 1.0
 */
public interface IoTDeviceQueryService {
    /**
     * Retrieves all IoT devices that belong to an organization.
     *
     * @param query query containing the organization identifier
     * @return organization devices, possibly empty
     */
    List<IoTDevice> handle(GetIoTDevicesByOrganizationIdQuery query);

    /**
     * Retrieves one IoT device by id and organization.
     *
     * @param query query containing organization and device identifiers
     * @return device when found, otherwise empty
     */
    Optional<IoTDevice> handle(GetIoTDeviceByIdAndOrganizationIdQuery query);
}
