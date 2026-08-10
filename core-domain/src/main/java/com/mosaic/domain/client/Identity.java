package com.mosaic.domain.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Identity(
        @NotBlank String fullName,
        @NotNull LocalDate dateOfBirth,
        Address currentAddress
) {
}
