package com.connectdeaf.controllers.dtos.response;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String phoneNumber
){
}
