package com.connectdeaf.controllers.dtos.requests;

import java.util.UUID;

public record AppointmentRequestDTO(
        UUID customerId,
        UUID professionalId,
        UUID serviceId,
        UUID scheduleId
) {
}