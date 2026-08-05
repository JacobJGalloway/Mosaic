package com.mosaic.domain.client;

import java.time.LocalDate;

public record HouseholdMember(
        String fullName,
        LocalDate dateOfBirth,
        String relationship,
        boolean isDriver
) {
}
