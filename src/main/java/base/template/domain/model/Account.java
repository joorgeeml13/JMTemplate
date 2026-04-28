package base.template.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Account {
    private UUID id;
    private String email;
    private String password;
    private List<Role> roles;
    private LocalDateTime createdAt;

    private Account() {
    }

    public Account(UUID id, String email, String password, List<Role> roles, LocalDateTime createdAt) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("error.account.invalid_email");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("error.account.weak_password");
        }

        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = (roles != null && !roles.isEmpty()) ? new ArrayList<>(roles) : new ArrayList<>(List.of(Role.USER));
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public void changePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }

    public void addRole(Role role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }
}