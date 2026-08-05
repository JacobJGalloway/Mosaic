package com.mosaic.domain.client;

public record Address(
        String street1,
        String street2,
        String city,
        String state,
        String postalCode,
        String country
) {
}
