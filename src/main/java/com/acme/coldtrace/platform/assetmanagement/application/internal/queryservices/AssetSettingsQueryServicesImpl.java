package com.acme.coldtrace.platform.assetmanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.AssetSettingsQueryService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetSettingsByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetEffectiveAssetSettingsByAssetIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for asset settings queries.
 *
 * @since 1.0
 */
@Service
public class AssetSettingsQueryServicesImpl implements AssetSettingsQueryService {
    private final AssetSettingsRepository assetSettingsRepository;
    private final AssetRepository assetRepository;

    public AssetSettingsQueryServicesImpl(
            AssetSettingsRepository assetSettingsRepository,
            AssetRepository assetRepository
    ) {
        this.assetSettingsRepository = assetSettingsRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public List<AssetSettings> handle(GetAssetSettingsByOrganizationIdQuery query) {
        return assetSettingsRepository.findAllByOrganizationId(query.organizationId());
    }

    @Override
    public Optional<AssetSettings> handle(GetEffectiveAssetSettingsByAssetIdQuery query) {
        if (assetRepository.findByIdAndOrganizationId(query.assetId(), query.organizationId()).isEmpty()) {
            return Optional.empty();
        }
        return assetSettingsRepository.findByOrganizationIdAndAssetId(query.organizationId(), query.assetId())
                .or(() -> assetSettingsRepository.findDefaultByOrganizationId(query.organizationId()));
    }
}
