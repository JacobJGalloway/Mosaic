package com.mosaic.domain.auth;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Baseline action-ID permission set for a role. A {@link User}'s effective
 * permissions are this set plus per-user {@code actionOverridesAdd} minus
 * {@code actionOverridesRemove} — see ARCHITECTURE.md's Auth Design.
 */
@Document(collection = "roles")
public class Role {

    @Id
    private String id;

    @NotBlank
    private String name;

    private Set<String> actionIds = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getActionIds() {
        return actionIds;
    }

    public void setActionIds(Set<String> actionIds) {
        this.actionIds = actionIds;
    }
}
