package com.acme.coldtrace.platform.assetmanagement.application.acl;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetSettingsRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.GatewayRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.IoTDeviceRepository;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application-layer implementation of the asset management ACL facade.
 *
 * @since 1.0
 */
@Service
public class AssetManagementContextFacadeImpl implements AssetManagementContextFacade {
    private final AssetRepository assetRepository;
    private final IoTDeviceRepository iotDeviceRepository;
    private final GatewayRepository gatewayRepository;
    private final AssetSettingsRepository assetSettingsRepository;

    public AssetManagementContextFacadeImpl(
            AssetRepository assetRepository,
            IoTDeviceRepository iotDeviceRepository,
            GatewayRepository gatewayRepository,
            AssetSettingsRepository assetSettingsRepository
    ) {
        this.assetRepository = assetRepository;
        this.iotDeviceRepository = iotDeviceRepository;
        this.gatewayRepository = gatewayRepository;
        this.assetSettingsRepository = assetSettingsRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AssetSnapshot> fetchAssetByIdAndOrganizationId(Long organizationId, Long assetId) {
        return assetRepository.findByIdAndOrganizationId(assetId, organizationId).map(this::toSnapshot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<IoTDeviceSnapshot> fetchIoTDeviceByIdAndOrganizationId(Long organizationId, Long iotDeviceId) {
        return iotDeviceRepository.findByIdAndOrganizationId(iotDeviceId, organizationId).map(this::toSnapshot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<GatewaySnapshot> fetchGatewayByIdAndOrganizationId(Long organizationId, Long gatewayId) {
        return gatewayRepository.findByIdAndOrganizationId(gatewayId, organizationId).map(this::toSnapshot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AssetSettingsSnapshot> fetchEffectiveAssetSettingsByAssetId(Long organizationId, Long assetId) {
        return assetSettingsRepository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .or(() -> assetSettingsRepository.findDefaultByOrganizationId(organizationId))
                .map(this::toSnapshot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IoTDeviceSnapshot> fetchAssignedIoTDevices(Long organizationId, Long assetId) {
        return iotDeviceRepository.findAllByOrganizationId(organizationId).stream()
                .filter(device -> device.getAssetId() != null)
                .filter(device -> assetId == null || assetId.equals(device.getAssetId()))
                .map(this::toSnapshot)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countAssetsByOrganizationId(Long organizationId) {
        return assetRepository.findAllByOrganizationId(organizationId).size();
    }

    private AssetSnapshot toSnapshot(Asset asset) {
        return new AssetSnapshot(asset.getId(), asset.getOrganizationId(), asset.getLocationId(), asset.getName());
    }

    private IoTDeviceSnapshot toSnapshot(IoTDevice device) {
        return new IoTDeviceSnapshot(
                device.getId(),
                device.getOrganizationId(),
                device.getGatewayId(),
                device.getAssetId(),
                device.getModel(),
                device.getStatus(),
                device.getMeasurementParameters()
        );
    }

    private GatewaySnapshot toSnapshot(Gateway gateway) {
        return new GatewaySnapshot(
                gateway.getId(),
                gateway.getOrganizationId(),
                gateway.getLocationId(),
                gateway.getStatus()
        );
    }

    private AssetSettingsSnapshot toSnapshot(AssetSettings settings) {
        return new AssetSettingsSnapshot(
                settings.getMinimumTemperature(),
                settings.getMaximumTemperature(),
                settings.getMinimumHumidity(),
                settings.getMaximumHumidity()
        );
    }
}
