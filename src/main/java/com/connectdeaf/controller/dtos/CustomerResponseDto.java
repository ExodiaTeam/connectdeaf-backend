package com.connectdeaf.controller.dtos;

import java.util.UUID;


public record CustomerResponseDto(
        UUID id,
        String name,
        String email,
        String numberPhone
) {
}
