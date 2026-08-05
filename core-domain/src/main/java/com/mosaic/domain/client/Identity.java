package com.mosaic.domain.client;

import java.time.LocalDate;

public record Identity(
        String fullName,
        LocalDate dateOfBirth,
        Address currentAddress
) {
}
