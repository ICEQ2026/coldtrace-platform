package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Location;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.LocationName;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters.LocationNamePersistenceConverter;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA persistence entity for organization locations.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"organization_id", "name"},
                name = Location.ORGANIZATION_ID_NAME_UNIQUE_CONSTRAINT
        )
})
public class LocationPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Convert(converter = LocationNamePersistenceConverter.class)
    @Column(nullable = false)
    private LocationName name;

    @Column(nullable = false)
    private String type;

    private String address;

    private String description;

    @Column(nullable = false)
    private String status;
}
