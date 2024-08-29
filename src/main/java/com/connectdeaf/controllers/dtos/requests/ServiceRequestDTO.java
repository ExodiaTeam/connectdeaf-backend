package com.connectdeaf.controllers.dtos.requests;

public record ServiceRequestDTO (
    String name,
    String description,
    Double value
) {
}
