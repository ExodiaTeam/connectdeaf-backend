package com.connectdeaf.controllers.dtos.response;

import java.util.UUID;

public record ServiceResponseDTO (
    UUID id,
    String name,
    String description,
    Double value,
    ProfessionalResponseDTO professional
) {
}