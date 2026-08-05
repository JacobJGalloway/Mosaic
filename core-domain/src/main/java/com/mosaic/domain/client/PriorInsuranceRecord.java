package com.mosaic.domain.client;

import java.time.LocalDate;

public record PriorInsuranceRecord(
        String carrierName,
        String policyNumber,
        LocalDate coverageStartDate,
        LocalDate coverageEndDate,
        boolean continuousCoverage
) {
}
