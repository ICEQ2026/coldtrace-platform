package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateAssetCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateAssetCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

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
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "uuid"}, name = Asset.ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT)
})
public class Asset extends AbstractAggregateRoot<Asset> {
    /** Unique constraint name shared with the persistence layer. */
    public static final String ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT = "uk_asset_organization_id_uuid";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double capacity;

    @Column
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

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
        this.uuid = command.uuid();
        this.type = command.type();
        this.name = command.name();
        this.capacity = command.capacity();
        this.description = command.description();
        this.status = command.status();
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
        this.uuid = command.uuid();
        this.type = command.type();
        this.name = command.name();
        this.capacity = command.capacity();
        this.description = command.description();
        this.status = command.status();
    }
}
