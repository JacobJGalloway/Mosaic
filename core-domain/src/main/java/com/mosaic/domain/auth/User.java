package com.mosaic.domain.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank
    private String username;

    @NotBlank
    private String passwordHash;

    @NotBlank
    private String roleId;

    private Set<String> actionOverridesAdd = new HashSet<>();
    private Set<String> actionOverridesRemove = new HashSet<>();

    /**
     * Bumped whenever this user's resolved actions change in a way that
     * demands immediate invalidation of their currently-held tokens
     * (termination, permission edits — including self-edits) rather than
     * waiting for natural token expiry. Checked against the JWT's
     * {@code tokenVersion} claim on every protected request.
     */
    private long tokenVersion = 0;

    private boolean active = true;

    private Instant createdAt;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Set<String> getActionOverridesAdd() {
        return actionOverridesAdd;
    }

    public void setActionOverridesAdd(Set<String> actionOverridesAdd) {
        this.actionOverridesAdd = actionOverridesAdd;
    }

    public Set<String> getActionOverridesRemove() {
        return actionOverridesRemove;
    }

    public void setActionOverridesRemove(Set<String> actionOverridesRemove) {
        this.actionOverridesRemove = actionOverridesRemove;
    }

    public long getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(long tokenVersion) {
        this.tokenVersion = tokenVersion;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
