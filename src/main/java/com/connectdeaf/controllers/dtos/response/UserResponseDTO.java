package com.connectdeaf.controllers.dtos.response;

import java.util.UUID;
import java.util.List;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        List<AddressResponseDTO> addresses
){
}
