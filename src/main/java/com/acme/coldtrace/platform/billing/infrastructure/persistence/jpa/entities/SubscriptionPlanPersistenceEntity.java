package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for subscription plans.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlanPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Integer monthlyPriceCents;

    @Column
    private String stripePriceId;

    @Column(nullable = false)
    private Boolean recommended;

    @Column
    private String recommendedLabel;

    @Column(nullable = false)
    private Boolean active;

    @Column
    private Integer maxLocations;

    @Column
    private Integer maxAssets;

    @Column
    private Integer maxIotDevices;

    @Column
    private Integer maxUsers;

    @Column
    private Integer historyRetentionDays;

    @Column(nullable = false)
    private Boolean allowsExports;

    @Column(nullable = false)
    private Boolean allowsMaintenance;

    @Column(nullable = false)
    private Boolean allowsAiGuidance;

    @Column(nullable = false)
    private Boolean allowsAiReportSummary;

    @ElementCollection
    @CollectionTable(
            name = "subscription_plan_included_features",
            joinColumns = @JoinColumn(name = "subscription_plan_id")
    )
    @Column(name = "feature", nullable = false)
    private List<String> includedFeatures = new ArrayList<>();
}
