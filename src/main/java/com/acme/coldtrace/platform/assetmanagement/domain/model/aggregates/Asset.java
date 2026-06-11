package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.events.AssetCreatedEvent;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.AssetUuid;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Asset aggregate for the asset management bounded context.
 * <p>
 * An asset represents a cold-chain business asset, such as a cold room or
 * refrigerated transport unit, owned by an organization and physically placed
 * in one of that organization's locations. The aggregate stores only the
 * location identifier because location ownership is validated by the
 * application service before the aggregate is persisted.
 * <p>
 * Asset uuid values are unique inside an organization. The database constraint
 * protects concurrent writes while the command service performs the same check
 * before persistence to produce a domain-specific failure response.
 *
 * @since 1.0
 */
@Getter
public class Asset extends AbstractDomainAggregateRoot<Asset> {
    /**
     * Unique constraint name shared with the infrastructure persistence layer.
     */
    public static final String ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT = "uk_asset_organization_id_uuid";

    private Long id;
    private Long organizationId;
    private Long locationId;
    private AssetUuid uuid;
    private String type;
    private String name;
    private Double capacity;
    private String description;
    private String status;

    protected Asset() {
    }

    /**
     * Creates an asset aggregate from a creation command.
     *
     * @param command command containing validated and normalized asset data
     * @see CreateAssetCommand
     */
    public Asset(CreateAssetCommand command) {
        this.organizationId = command.organizationId();
        this.locationId = command.locationId();
        this.uuid = new AssetUuid(command.uuid());
        this.type = command.type();
        this.name = command.name();
        this.capacity = command.capacity();
        this.description = command.description();
        this.status = command.status();
    }

    /**
     * Rebuilds an asset aggregate from persisted state.
     * <p>
     * This constructor is used by repository adapters when translating an
     * infrastructure persistence entity into the domain model. It keeps the
     * aggregate free from JPA while still preserving identity for application
     * services and REST assemblers.
     *
     * @param id asset identifier assigned by persistence
     * @param organizationId organization that owns the asset
     * @param locationId organization location where the asset is placed
     * @param uuid business identifier unique inside the organization
     * @param type asset type selected by the business workflow
     * @param name human-readable asset name
     * @param capacity storage or transport capacity represented by the asset
     * @param description optional operational notes
     * @param status current operational status
     */
    public Asset(
            Long id,
            Long organizationId,
            Long locationId,
            AssetUuid uuid,
            String type,
            String name,
            Double capacity,
            String description,
            String status
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.locationId = locationId;
        this.uuid = uuid;
        this.type = type;
        this.name = name;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
    }

    /**
     * Updates mutable asset fields with data from the update command.
     * <p>
     * The organization identifier is intentionally not changed. The asset
     * remains inside the organization boundary selected by the route, while the
     * location may change only after the command service confirms the new
     * location also belongs to that same organization.
     *
     * @param command command containing validated and normalized asset data
     * @see UpdateAssetCommand
     */
    public void update(UpdateAssetCommand command) {
        this.locationId = command.locationId();
        this.uuid = new AssetUuid(command.uuid());
        this.type = command.type();
        this.name = command.name();
        this.capacity = command.capacity();
        this.description = command.description();
        this.status = command.status();
    }

    /**
     * Returns the asset uuid as a string for application and REST consumers.
     *
     * @return string representation of the asset uuid
     */
    public String getUuid() {
        return this.uuid.value();
    }

    /**
     * Returns the strongly typed asset uuid value object.
     *
     * @return asset uuid value object
     */
    public AssetUuid getUuidValue() {
        return this.uuid;
    }

    /**
     * Registers the domain event emitted after a new asset is persisted.
     */
    public void onCreated() {
        registerDomainEvent(AssetCreatedEvent.from(this));
    }
}
