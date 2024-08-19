package com.connectdeaf.infrastructure.controllers.dtos.requests;


import java.util.List;

public record UserRequestDTO(
        String name,
        String email,
        String password,
        String phoneNumber,
        List<AddressRequestDTO> addresses
) {
}
