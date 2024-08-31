package com.connectdeaf.controllers.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentResponseDTO(
    UUID id,
    UserResponseDTO customer,
    ProfessionalResponseDTO professional,
    ServiceResponseDTO service,
    LocalDate date,
    LocalTime time,
    String status
) {  
}
