package com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.CreateGatewayCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.UpdateGatewayCommand;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.GatewayUuid;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Gateway aggregate for the asset management context.
 * It represents an edge gateway installed in a location.
 *
 * @since 1.0
 */
@Getter
public class Gateway extends AbstractDomainAggregateRoot<Gateway> {
    /**
     * Unique constraint name shared with the infrastructure persistence layer.
     */
    public static final String ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT = "uk_gateway_organization_id_uuid";

    private Long id;
    private Long organizationId;
    private Long locationId;
    private GatewayUuid uuid;
    private String name;
    private String network;
    private String status;

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
        this.uuid = new GatewayUuid(command.uuid());
        this.name = command.name();
        this.network = command.network();
        this.status = command.status();
    }

    /**
     * Rebuilds a gateway aggregate from persistence state.
     *
     * @param id gateway identifier assigned by persistence
     * @param organizationId organization that owns the gateway
     * @param locationId location where the gateway is installed
     * @param uuid gateway business uuid
     * @param name gateway name
     * @param network network label used by operators
     * @param status current gateway status
     */
    public Gateway(
            Long id,
            Long organizationId,
            Long locationId,
            GatewayUuid uuid,
            String name,
            String network,
            String status
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.locationId = locationId;
        this.uuid = uuid;
        this.name = name;
        this.network = network;
        this.status = status;
    }

    /**
     * Updates the gateway with command data.
     *
     * @param command command containing updated gateway data
     * @see UpdateGatewayCommand
     */
    public void update(UpdateGatewayCommand command) {
        this.locationId = command.locationId();
        this.uuid = new GatewayUuid(command.uuid());
        this.name = command.name();
        this.network = command.network();
        this.status = command.status();
    }

    /**
     * Returns the gateway uuid as a string for application and REST consumers.
     *
     * @return gateway business uuid
     */
    public String getUuid() {
        return this.uuid.value();
    }

    /**
     * Returns the strongly typed gateway uuid value object.
     *
     * @return gateway uuid value object
     */
    public GatewayUuid getUuidValue() {
        return this.uuid;
    }
}
