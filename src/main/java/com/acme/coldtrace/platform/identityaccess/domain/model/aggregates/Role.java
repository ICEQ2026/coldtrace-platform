package com.acme.coldtrace.platform.identityaccess.domain.model.aggregates;

import com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects.Permission;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Role aggregate for authorization metadata used by the frontend.
 * A role groups permissions and provides stable role names such as
 * {@code super-admin}, {@code administrator}, and {@code operator}.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String label;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    private List<Permission> permissions = new ArrayList<>();

    protected Role() {
    }

    /**
     * Creates a role with a display label and permission set.
     *
     * @param name stable role identifier
     * @param label display label for the role
     * @param permissions permissions assigned to the role
     */
    public Role(String name, String label, List<Permission> permissions) {
        this.name = name.trim();
        this.label = label.trim();
        this.permissions = new ArrayList<>(permissions);
    }
}
