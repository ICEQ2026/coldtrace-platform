package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.GatewayUuid;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters.GatewayUuidPersistenceConverter;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA persistence entity for edge gateways.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "gateways", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"organization_id", "uuid"},
                name = Gateway.ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT
        )
})
public class GatewayPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long locationId;

    @Convert(converter = GatewayUuidPersistenceConverter.class)
    @Column(nullable = false)
    private GatewayUuid uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String network;

    @Column(nullable = false)
    private String status;
}
