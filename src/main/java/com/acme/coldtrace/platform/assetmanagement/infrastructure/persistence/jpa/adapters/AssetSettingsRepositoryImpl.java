package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetSettingsRepository;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.assemblers.AssetSettingsPersistenceAssembler;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.repositories.AssetSettingsPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the asset settings domain repository.
 *
 * @since 1.0
 */
@Repository
@Transactional(readOnly = true)
public class AssetSettingsRepositoryImpl implements AssetSettingsRepository {
    private final AssetSettingsPersistenceRepository assetSettingsPersistenceRepository;

    public AssetSettingsRepositoryImpl(AssetSettingsPersistenceRepository assetSettingsPersistenceRepository) {
        this.assetSettingsPersistenceRepository = assetSettingsPersistenceRepository;
    }

    @Override
    public List<AssetSettings> findAllByOrganizationId(Long organizationId) {
        return assetSettingsPersistenceRepository.findAllByOrganizationId(organizationId).stream()
                .map(AssetSettingsPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<AssetSettings> findByOrganizationIdAndAssetId(Long organizationId, Long assetId) {
        return assetSettingsPersistenceRepository.findByOrganizationIdAndAssetId(organizationId, assetId)
                .map(AssetSettingsPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<AssetSettings> findDefaultByOrganizationId(Long organizationId) {
        return assetSettingsPersistenceRepository.findFirstByOrganizationIdAndAssetIdIsNull(organizationId)
                .map(AssetSettingsPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional
    public AssetSettings save(AssetSettings assetSettings) {
        if (assetSettings.getId() == null) {
            var entity = AssetSettingsPersistenceAssembler.toPersistenceFromDomain(assetSettings);
            return AssetSettingsPersistenceAssembler.toDomainFromPersistence(
                    assetSettingsPersistenceRepository.save(entity)
            );
        }

        var entity = assetSettingsPersistenceRepository.findById(assetSettings.getId())
                .orElseGet(() -> AssetSettingsPersistenceAssembler.toPersistenceFromDomain(assetSettings));
        AssetSettingsPersistenceAssembler.copyDomainState(assetSettings, entity);
        return AssetSettingsPersistenceAssembler.toDomainFromPersistence(
                assetSettingsPersistenceRepository.save(entity)
        );
    }
}
