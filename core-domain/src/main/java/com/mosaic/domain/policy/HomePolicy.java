package com.mosaic.domain.policy;

import com.mosaic.domain.client.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class HomePolicy extends Policy {

    @NotNull
    @Valid
    private Address propertyAddress;

    @NotBlank
    private String constructionType;

    @Positive
    private int yearBuilt;

    @NotNull
    private BigDecimal dwellingCoverageAmount;

    @NotNull
    private BigDecimal personalPropertyCoverageAmount;

    @NotNull
    private BigDecimal liabilityCoverageAmount;

    public Address getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(Address propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public String getConstructionType() {
        return constructionType;
    }

    public void setConstructionType(String constructionType) {
        this.constructionType = constructionType;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public BigDecimal getDwellingCoverageAmount() {
        return dwellingCoverageAmount;
    }

    public void setDwellingCoverageAmount(BigDecimal dwellingCoverageAmount) {
        this.dwellingCoverageAmount = dwellingCoverageAmount;
    }

    public BigDecimal getPersonalPropertyCoverageAmount() {
        return personalPropertyCoverageAmount;
    }

    public void setPersonalPropertyCoverageAmount(BigDecimal personalPropertyCoverageAmount) {
        this.personalPropertyCoverageAmount = personalPropertyCoverageAmount;
    }

    public BigDecimal getLiabilityCoverageAmount() {
        return liabilityCoverageAmount;
    }

    public void setLiabilityCoverageAmount(BigDecimal liabilityCoverageAmount) {
        this.liabilityCoverageAmount = liabilityCoverageAmount;
    }
}
