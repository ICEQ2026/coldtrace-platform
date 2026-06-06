package com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Permission {
    private Long id;
    private String resource;
    private String action;
    private String description;

    protected Permission() {
    }

    public Permission(Long id, String resource, String action, String description) {
        this.id = id;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }
}
