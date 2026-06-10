package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.AssetUuid;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters.AssetUuidPersistenceConverter;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA persistence entity for asset records.
 * <p>
 * This class owns table, column, converter and auditing concerns for assets.
 * The domain aggregate remains persistence-agnostic and is rebuilt through the
 * asset persistence assembler when repositories return data to the application
 * layer.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"organization_id", "uuid"},
                name = Asset.ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT
        )
})
public class AssetPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long locationId;

    @Convert(converter = AssetUuidPersistenceConverter.class)
    @Column(nullable = false)
    private AssetUuid uuid;

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
}
