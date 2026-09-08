package com.mosaic.domain.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Vehicle(
        @NotBlank String vin,
        @NotNull Integer year,
        @NotBlank String make,
        @NotBlank String model
) {
}
