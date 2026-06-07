package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateGatewayCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateGatewayCommand;
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
 * Gateway aggregate for the asset management context.
 * It represents an edge gateway installed in a location.
 *
 * @since 1.0
 */
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "gateways", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "uuid"}, name = Gateway.ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT)
})
public class Gateway extends AbstractAggregateRoot<Gateway> {
    /** Unique constraint name shared with the persistence layer. */
    public static final String ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT = "uk_gateway_organization_id_uuid";

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
    private String name;

    @Column(nullable = false)
    private String network;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    protected Gateway() {
    }

    /**
     * Creates a gateway from a create command.
     *
     * @param command command containing gateway data
     * @see CreateGatewayCommand
     */
    public Gateway(CreateGatewayCommand command) {
        this.organizationId = command.organizationId();
        this.locationId = command.locationId();
        this.uuid = command.uuid();
        this.name = command.name();
        this.network = command.network();
        this.status = command.status();
    }

    /**
     * Updates the gateway with command data.
     *
     * @param command command containing updated gateway data
     * @see UpdateGatewayCommand
     */
    public void update(UpdateGatewayCommand command) {
        this.locationId = command.locationId();
        this.uuid = command.uuid();
        this.name = command.name();
        this.network = command.network();
        this.status = command.status();
    }
}
