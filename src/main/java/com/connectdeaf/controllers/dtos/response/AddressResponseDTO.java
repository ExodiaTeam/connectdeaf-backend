package com.connectdeaf.controllers.dtos.response;

public record AddressResponseDTO(
        String street,
        String city,
        String state,
        String zipCode
) {
}