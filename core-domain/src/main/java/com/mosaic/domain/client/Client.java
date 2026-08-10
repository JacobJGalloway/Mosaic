package com.mosaic.domain.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "clients")
public class Client {

    @Id
    private String id;

    @NotNull
    @Valid
    private Identity identity;

    @NotNull
    @Valid
    private ContactInfo contact;
    private List<PriorInsuranceRecord> priorInsurance = new ArrayList<>();
    private List<HouseholdMember> household = new ArrayList<>();
    private List<ClientDocument> documents = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }

    public List<PriorInsuranceRecord> getPriorInsurance() {
        return priorInsurance;
    }

    public void setPriorInsurance(List<PriorInsuranceRecord> priorInsurance) {
        this.priorInsurance = priorInsurance;
    }

    public List<HouseholdMember> getHousehold() {
        return household;
    }

    public void setHousehold(List<HouseholdMember> household) {
        this.household = household;
    }

    public List<ClientDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ClientDocument> documents) {
        this.documents = documents;
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
