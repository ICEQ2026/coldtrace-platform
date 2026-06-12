package com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.AssetSnapshot;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.events.TechnicalServiceRequestCreatedEvent;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Corrective technical service request aggregate.
 * <p>
 * A technical service request records corrective work for an organization
 * asset. It may reference an incident, stores the asset snapshot needed by
 * maintenance workflows and owns the lifecycle status used by the backend.
 *
 * @since 1.0
 */
@Getter
public class TechnicalServiceRequest extends AbstractDomainAggregateRoot<TechnicalServiceRequest> {
    /** Supported lifecycle statuses exposed by the technical service workflow. */
    public static final Set<String> ALLOWED_STATUSES = Set.of("open", "in_progress", "closed", "canceled");
    private static final Set<String> TERMINAL_STATUSES = Set.of("closed", "canceled");

    private Long id;
    private Long organizationId;
    private String code;
    private Long assetId;
    private Long assetLocationId;
    private String assetName;
    private Long incidentId;
    private String issueDescription;
    private String priority;
    private String status;
    private String requestedBy;
    private OffsetDateTime requestedAt;
    private OffsetDateTime closedAt;
    private String closureSummary;
    private String evidence;
    private String closedBy;

    protected TechnicalServiceRequest() {
    }

    /**
     * Creates a technical service request from a command and asset snapshot.
     *
     * @param command validated creation command
     * @param asset asset snapshot owned by the organization
     */
    public TechnicalServiceRequest(CreateTechnicalServiceRequestCommand command, AssetSnapshot asset) {
        this.organizationId = command.organizationId();
        this.code = "TSR-" + UUID.randomUUID();
        this.assetId = command.assetId();
        this.assetLocationId = asset.locationId();
        this.assetName = asset.name();
        this.incidentId = command.incidentId();
        this.issueDescription = command.issueDescription();
        this.priority = command.priority();
        this.status = "open";
        this.requestedBy = command.requestedBy();
        this.requestedAt = OffsetDateTime.now();
    }

    /**
     * Rebuilds a technical service request aggregate from persisted state.
     *
     * @param id persistence identifier
     * @param organizationId owning organization identifier
     * @param code public technical service request code
     * @param assetId serviced asset identifier
     * @param assetLocationId asset location snapshot
     * @param assetName asset name snapshot
     * @param incidentId optional related incident identifier
     * @param issueDescription reported problem description
     * @param priority service priority
     * @param status lifecycle status
     * @param requestedBy optional requester name or email
     * @param requestedAt request creation timestamp
     * @param closedAt closure timestamp
     * @param closureSummary closure summary
     * @param evidence closure evidence
     * @param closedBy actor who closed the request
     */
    public TechnicalServiceRequest(
            Long id,
            Long organizationId,
            String code,
            Long assetId,
            Long assetLocationId,
            String assetName,
            Long incidentId,
            String issueDescription,
            String priority,
            String status,
            String requestedBy,
            OffsetDateTime requestedAt,
            OffsetDateTime closedAt,
            String closureSummary,
            String evidence,
            String closedBy
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.code = code;
        this.assetId = assetId;
        this.assetLocationId = assetLocationId;
        this.assetName = assetName;
        this.incidentId = incidentId;
        this.issueDescription = issueDescription;
        this.priority = priority;
        this.status = status;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
        this.closedAt = closedAt;
        this.closureSummary = closureSummary;
        this.evidence = evidence;
        this.closedBy = closedBy;
    }

    /**
     * Indicates whether the current lifecycle status can transition to the
     * requested status.
     *
     * @param requestedStatus requested lifecycle status
     * @return true when the transition is allowed
     */
    public boolean canTransitionTo(String requestedStatus) {
        if (status.equals(requestedStatus)) {
            return true;
        }
        if (TERMINAL_STATUSES.contains(status)) {
            return false;
        }
        if ("open".equals(status)) {
            return Set.of("in_progress", "closed", "canceled").contains(requestedStatus);
        }
        if ("in_progress".equals(status)) {
            return Set.of("closed", "canceled").contains(requestedStatus);
        }
        return false;
    }

    /**
     * Updates lifecycle status and captures closure metadata when closing.
     *
     * @param command validated status update command
     */
    public void updateStatus(UpdateTechnicalServiceRequestStatusCommand command) {
        this.status = command.status();
        if ("closed".equals(command.status())) {
            this.closedAt = OffsetDateTime.now();
            this.closureSummary = command.closureSummary();
            this.evidence = command.evidence();
            this.closedBy = command.closedBy();
        }
    }

    /**
     * Registers the domain event emitted after a request is created.
     */
    public void onCreated() {
        registerDomainEvent(TechnicalServiceRequestCreatedEvent.from(this));
    }
}
