package com.acme.coldtrace.platform.identityaccess.domain.model.aggregates;

import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateUserCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private Long organizationUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long roleId;

    protected User() {
    }

    public User(CreateUserCommand command) {
        this.uuid = command.uuid();
        this.organizationUserId = command.organizationUserId();
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.email = command.email();
        this.organizationId = command.organizationId();
        this.roleId = command.roleId();
    }
}
