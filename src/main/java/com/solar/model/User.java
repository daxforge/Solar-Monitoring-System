package com.solar.model;

import com.solar.enums.Role;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model representing system users (ADMIN or HOMEOWNER).
 * Fully encapsulated with Java Bean conventions.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;
    private boolean active;

    public User() {
        this.active = true;
        this.role = Role.HOMEOWNER;
        this.createdAt = LocalDateTime.now();
    }

    public User(Long id, String name, String email, String passwordHash, Role role,
                LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.HOMEOWNER;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.active = active;
    }

    // Helper methods for easy role checking in EL / JSP
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isHomeowner() {
        return this.role == Role.HOMEOWNER;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
