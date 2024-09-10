package com.connectdeaf.controllers.dtos.response;

import java.util.UUID;

public record AppointmentResponseDTO(
        UUID id,
        UserResponseDTO customer,
        ProfessionalResponseDTO professional,
        ServiceResponseDTO service,
        ScheduleResponseDTO schedule,  // Inclui os detalhes do horário agendado
        String status
) {
}
