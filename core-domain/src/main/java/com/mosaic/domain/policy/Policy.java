package com.mosaic.domain.policy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Common shape shared by every policy line (home, auto, and — per
 * OVERVIEW.md's Domain Scope RV-extensibility note — a future recreational-
 * vehicle shape without re-architecting this base). Stored in its own
 * `policies` collection, referenced from a client by {@link #clientId}
 * rather than embedded, per Jacob's 2026-09-08 direction.
 * <p>
 * The Jackson type-info annotations below are required so REST/MCP callers
 * can POST a bare {@code Policy} JSON body and have it deserialize to the
 * correct concrete subtype — this is separate from, and in addition to,
 * Spring Data Mongo's own `_class` discriminator used for storage.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "policyType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HomePolicy.class, name = "HOME"),
        @JsonSubTypes.Type(value = AutoPolicy.class, name = "AUTO")
})
@Document(collection = "policies")
public abstract class Policy {

    @Id
    private String id;

    @NotBlank
    private String clientId;

    @NotBlank
    private String policyNumber;

    @NotNull
    private PolicyStatus status = PolicyStatus.PENDING;

    @NotNull
    private LocalDate effectiveDate;

    private LocalDate expirationDate;

    @NotNull
    private BigDecimal premiumAmount;

    private Instant createdAt;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
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
