package com.connectdeaf.controllers.dtos.response;

import java.util.UUID;

import com.connectdeaf.domain.professional.Professional;

public record ServiceResponseDTO (
    UUID id,
    String name,
    String description,
    Double value,
    Professional professional
) {
}