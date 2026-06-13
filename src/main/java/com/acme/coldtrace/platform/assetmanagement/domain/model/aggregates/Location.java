package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateLocationCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateLocationCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.LocationName;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Location aggregate for the asset management context.
 * It represents an operational place where assets and gateways are registered.
 *
 * @since 1.0
 */
@Getter
public class Location extends AbstractDomainAggregateRoot<Location> {
    /**
     * Unique constraint name shared with the infrastructure persistence layer.
     */
    public static final String ORGANIZATION_ID_NAME_UNIQUE_CONSTRAINT = "uk_location_organization_id_name";

    private Long id;
    private Long organizationId;
    private LocationName name;
    private String type;
    private String address;
    private String description;
    private String status;

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
        this.name = new LocationName(command.name());
        this.type = command.type();
        this.address = command.address();
        this.description = command.description();
        this.status = command.status();
    }

    /**
     * Rebuilds a location aggregate from persistence state.
     *
     * @param id location identifier assigned by persistence
     * @param organizationId organization that owns the location
     * @param name unique location name inside the organization
     * @param type operational location type
     * @param address optional physical address
     * @param description optional operational notes
     * @param status current location status
     */
    public Location(
            Long id,
            Long organizationId,
            LocationName name,
            String type,
            String address,
            String description,
            String status
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.type = type;
        this.address = address;
        this.description = description;
        this.status = status;
    }

    /**
     * Updates the location with command data.
     *
     * @param command command containing updated location data
     * @see UpdateLocationCommand
     */
    public void update(UpdateLocationCommand command) {
        this.name = new LocationName(command.name());
        this.type = command.type();
        this.address = command.address();
        this.description = command.description();
        this.status = command.status();
    }

    /**
     * Returns the location name as a string for application and REST consumers.
     *
     * @return location name
     */
    public String getName() {
        return this.name.value();
    }

    /**
     * Returns the strongly typed location name value object.
     *
     * @return location name value object
     */
    public LocationName getNameValue() {
        return this.name;
    }
}
