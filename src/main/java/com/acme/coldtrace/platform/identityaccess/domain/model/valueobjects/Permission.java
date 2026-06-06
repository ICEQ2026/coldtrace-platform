package com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * Permission value object embedded in a role.
 * It mirrors the frontend permission contract with resource, action, and
 * translation-key description fields.
 *
 * @since 1.0
 */
@Getter
@Embeddable
public class Permission {
    private Long id;
    private String resource;
    private String action;
    private String description;

    protected Permission() {
    }

    /**
     * Creates a permission value object.
     *
     * @param id permission identifier used by the frontend resource contract
     * @param resource protected resource name
     * @param action action allowed over the resource
     * @param description translation key used as permission description
     */
    public Permission(Long id, String resource, String action, String description) {
        this.id = id;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }
}
