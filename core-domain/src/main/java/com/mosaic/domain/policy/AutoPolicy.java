package com.mosaic.domain.policy;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AutoPolicy extends Policy {

    @NotEmpty
    @Valid
    private List<Vehicle> vehicles = new ArrayList<>();

    @NotNull
    private BigDecimal liabilityCoverageAmount;

    @NotNull
    private BigDecimal comprehensiveDeductible;

    @NotNull
    private BigDecimal collisionDeductible;

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public BigDecimal getLiabilityCoverageAmount() {
        return liabilityCoverageAmount;
    }

    public void setLiabilityCoverageAmount(BigDecimal liabilityCoverageAmount) {
        this.liabilityCoverageAmount = liabilityCoverageAmount;
    }

    public BigDecimal getComprehensiveDeductible() {
        return comprehensiveDeductible;
    }

    public void setComprehensiveDeductible(BigDecimal comprehensiveDeductible) {
        this.comprehensiveDeductible = comprehensiveDeductible;
    }

    public BigDecimal getCollisionDeductible() {
        return collisionDeductible;
    }

    public void setCollisionDeductible(BigDecimal collisionDeductible) {
        this.collisionDeductible = collisionDeductible;
    }
}
