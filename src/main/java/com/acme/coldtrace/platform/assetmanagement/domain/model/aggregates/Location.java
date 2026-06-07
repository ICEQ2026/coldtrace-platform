package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateLocationCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateLocationCommand;
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
 * Location aggregate for the asset management context.
 * It represents an operational place where assets and gateways are registered.
 *
 * @since 1.0
 */
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "name"}, name = Location.ORGANIZATION_ID_NAME_UNIQUE_CONSTRAINT)
})
public class Location extends AbstractAggregateRoot<Location> {
    /** Unique constraint name shared with the persistence layer. */
    public static final String ORGANIZATION_ID_NAME_UNIQUE_CONSTRAINT = "uk_location_organization_id_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    private String address;

    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    protected Location() {
    }

    /**
     * Creates a location from a create command.
     *
     * @param command command containing location data
     * @see CreateLocationCommand
     */
    public Location(CreateLocationCommand command) {
        this.organizationId = command.organizationId();
        this.name = command.name();
        this.type = command.type();
        this.address = command.address();
        this.description = command.description();
        this.status = command.status();
    }

    /**
     * Updates the location with command data.
     *
     * @param command command containing updated location data
     * @see UpdateLocationCommand
     */
    public void update(UpdateLocationCommand command) {
        this.name = command.name();
        this.type = command.type();
        this.address = command.address();
        this.description = command.description();
        this.status = command.status();
    }
}
