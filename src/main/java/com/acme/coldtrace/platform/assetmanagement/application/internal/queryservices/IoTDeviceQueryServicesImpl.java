package com.acme.coldtrace.platform.assetmanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.IoTDeviceQueryService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetIoTDeviceByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetIoTDevicesByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.IoTDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for IoT device query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class IoTDeviceQueryServicesImpl implements IoTDeviceQueryService {
    private final IoTDeviceRepository iotDeviceRepository;

    public IoTDeviceQueryServicesImpl(IoTDeviceRepository iotDeviceRepository) {
        this.iotDeviceRepository = iotDeviceRepository;
    }

    /**
     * Retrieves IoT devices from persistence by organization.
     *
     * @param query query object containing the organization identifier
     * @return devices for the organization, possibly empty
     * @see GetIoTDevicesByOrganizationIdQuery
     */
    @Override
    public List<IoTDevice> handle(GetIoTDevicesByOrganizationIdQuery query) {
        log.debug("Querying IoT devices by organizationId={}", query.organizationId());
        return iotDeviceRepository.findAllByOrganizationId(query.organizationId());
    }

    /**
     * Retrieves one IoT device from persistence by id and organization.
     *
     * @param query query object containing organization and device identifiers
     * @return device when found, otherwise empty
     * @see GetIoTDeviceByIdAndOrganizationIdQuery
     */
    @Override
    public Optional<IoTDevice> handle(GetIoTDeviceByIdAndOrganizationIdQuery query) {
        log.debug("Querying IoT device by organizationId={}, iotDeviceId={}",
                query.organizationId(), query.iotDeviceId());
        return iotDeviceRepository.findByIdAndOrganizationId(query.iotDeviceId(), query.organizationId());
    }
}
