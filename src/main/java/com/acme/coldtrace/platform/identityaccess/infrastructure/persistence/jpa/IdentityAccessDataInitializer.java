package com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects.Permission;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdentityAccessDataInitializer implements ApplicationRunner {
    private static final Permission MANAGE_ADMINISTRATORS = new Permission(
            1L,
            "administrators",
            "manage",
            "roles-permissions.permissions.manage-administrators"
    );
    private static final Permission MANAGE_USERS = new Permission(
            2L,
            "users",
            "manage",
            "roles-permissions.permissions.manage-users"
    );
    private static final Permission MANAGE_ASSETS = new Permission(
            3L,
            "assets",
            "manage",
            "roles-permissions.permissions.manage-assets"
    );
    private static final Permission VIEW_REPORTS = new Permission(
            4L,
            "reports",
            "view",
            "roles-permissions.permissions.view-reports"
    );
    private static final Permission RESOLVE_ALERTS = new Permission(
            5L,
            "alerts",
            "update",
            "roles-permissions.permissions.resolve-alerts"
    );
    private static final Permission MONITOR_ASSETS = new Permission(
            6L,
            "assets",
            "view",
            "roles-permissions.permissions.monitor-assets"
    );
    private static final Permission READ_ONLY = new Permission(
            7L,
            "operations",
            "view",
            "roles-permissions.permissions.read-only"
    );

    private final RoleRepository roleRepository;

    public IdentityAccessDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedRole(
                "super-admin",
                "Super Administrator",
                List.of(
                        MANAGE_ADMINISTRATORS,
                        MANAGE_USERS,
                        MANAGE_ASSETS,
                        VIEW_REPORTS,
                        RESOLVE_ALERTS,
                        MONITOR_ASSETS,
                        READ_ONLY
                )
        );
        seedRole(
                "administrator",
                "Administrator",
                List.of(MANAGE_USERS, MANAGE_ASSETS, VIEW_REPORTS, RESOLVE_ALERTS, MONITOR_ASSETS, READ_ONLY)
        );
        seedRole(
                "operations-manager",
                "Operations Manager",
                List.of(MANAGE_ASSETS, RESOLVE_ALERTS, VIEW_REPORTS)
        );
        seedRole(
                "operator",
                "Operator",
                List.of(MONITOR_ASSETS, RESOLVE_ALERTS)
        );
        seedRole(
                "auditor",
                "Auditor",
                List.of(VIEW_REPORTS, READ_ONLY)
        );
    }

    private void seedRole(String name, String label, List<Permission> permissions) {
        if (roleRepository.existsByName(name)) {
            return;
        }
        roleRepository.save(new Role(name, label, permissions));
    }
}
