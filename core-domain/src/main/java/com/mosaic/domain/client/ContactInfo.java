package com.mosaic.domain.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactInfo(
        @NotBlank String phone,
        @NotBlank @Email String email
) {
}
