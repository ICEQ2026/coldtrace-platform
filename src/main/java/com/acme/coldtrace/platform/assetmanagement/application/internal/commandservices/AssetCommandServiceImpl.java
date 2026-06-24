package com.acme.coldtrace.platform.assetmanagement.application.internal.commandservices;

import com.acme.coldtrace.platform.assetmanagement.application.commandservices.AssetCommandFailure;
import com.acme.coldtrace.platform.assetmanagement.application.commandservices.AssetCommandService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.LocationRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset.ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT;

/**
 * Application service implementation for asset command operations.
 * <p>
 * This service coordinates persistence-level checks that require repositories:
 * organization existence, location ownership and asset uuid uniqueness inside
 * the organization. The aggregate remains focused on its own state while this
 * service protects cross-aggregate invariants required by the REST use cases.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class AssetCommandServiceImpl implements AssetCommandService {
    private static final String DUPLICATE_ASSET_CONSTRAINT = ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT;

    private final AssetRepository assetRepository;
    private final LocationRepository locationRepository;
    private final OrganizationRepository organizationRepository;

    public AssetCommandServiceImpl(
            AssetRepository assetRepository,
            LocationRepository locationRepository,
            OrganizationRepository organizationRepository
    ) {
        this.assetRepository = assetRepository;
        this.locationRepository = locationRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Handles creation of an asset aggregate.
     *
     * @param command command containing asset data
     * @return success with created asset or failure with an asset command error
     * @see CreateAssetCommand
     */
    @Override
    @Transactional
    public Result<Asset, AssetCommandFailure> handle(CreateAssetCommand command) {
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for asset creation: organizationId={}", command.organizationId());
            return Result.failure(new AssetCommandFailure.OrganizationNotFound());
        }
        if (locationRepository.findByIdAndOrganizationId(command.locationId(), command.organizationId()).isEmpty()) {
            log.warn("Location not found for asset creation: organizationId={}, locationId={}",
                    command.organizationId(), command.locationId());
            return Result.failure(new AssetCommandFailure.LocationNotFound());
        }
        if (assetRepository.existsByOrganizationIdAndUuid(command.organizationId(), command.uuid())) {
            log.warn("Duplicate asset uuid detected: organizationId={}, uuid={}",
                    command.organizationId(), command.uuid());
            return Result.failure(new AssetCommandFailure.DuplicateUuid());
        }
        try {
            var asset = assetRepository.save(new Asset(command));
            log.info("Asset created: id={}, organizationId={}, uuid={}",
                    asset.getId(), asset.getOrganizationId(), asset.getUuid());
            return Result.success(asset);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateAssetViolation(exception)) {
                log.warn("Duplicate asset detected by constraint: organizationId={}, uuid={}",
                        command.organizationId(), command.uuid());
                return Result.failure(new AssetCommandFailure.DuplicateUuid());
            }
            throw exception;
        }
    }

    /**
     * Handles update of an asset aggregate.
     *
     * @param command command containing updated asset data
     * @return success with updated asset or failure with an asset command error
     * @see UpdateAssetCommand
     */
    @Override
    @Transactional
    public Result<Asset, AssetCommandFailure> handle(UpdateAssetCommand command) {
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for asset update: organizationId={}", command.organizationId());
            return Result.failure(new AssetCommandFailure.OrganizationNotFound());
        }
        if (locationRepository.findByIdAndOrganizationId(command.locationId(), command.organizationId()).isEmpty()) {
            log.warn("Location not found for asset update: organizationId={}, locationId={}",
                    command.organizationId(), command.locationId());
            return Result.failure(new AssetCommandFailure.LocationNotFound());
        }
        var asset = assetRepository.findByIdAndOrganizationId(command.assetId(), command.organizationId());
        if (asset.isEmpty()) {
            log.warn("Asset not found for update: organizationId={}, assetId={}",
                    command.organizationId(), command.assetId());
            return Result.failure(new AssetCommandFailure.AssetNotFound());
        }
        if (assetRepository.existsByOrganizationIdAndUuidAndIdNot(
                command.organizationId(), command.uuid(), command.assetId())) {
            log.warn("Duplicate asset uuid detected for update: organizationId={}, assetId={}, uuid={}",
                    command.organizationId(), command.assetId(), command.uuid());
            return Result.failure(new AssetCommandFailure.DuplicateUuid());
        }
        try {
            asset.get().update(command);
            var updatedAsset = assetRepository.save(asset.get());
            log.info("Asset updated: id={}, organizationId={}, uuid={}",
                    updatedAsset.getId(), updatedAsset.getOrganizationId(), updatedAsset.getUuid());
            return Result.success(updatedAsset);
        } catch (DataIntegrityViolationException exception) {
            if (isDuplicateAssetViolation(exception)) {
                log.warn("Duplicate asset detected by constraint during update: organizationId={}, assetId={}, uuid={}",
                        command.organizationId(), command.assetId(), command.uuid());
                return Result.failure(new AssetCommandFailure.DuplicateUuid());
            }
            throw exception;
        }
    }

    private boolean isDuplicateAssetViolation(DataIntegrityViolationException exception) {
        Throwable violationCause = exception;
        while (violationCause != null) {
            String message = violationCause.getMessage();
            if (message != null && message.contains(DUPLICATE_ASSET_CONSTRAINT)) {
                return true;
            }
            violationCause = violationCause.getCause();
        }
        return false;
    }
}
