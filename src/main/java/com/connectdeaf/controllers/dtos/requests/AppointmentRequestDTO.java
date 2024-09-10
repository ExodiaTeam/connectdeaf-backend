package com.connectdeaf.controllers.dtos.requests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentRequestDTO(
        UUID customerId,
        UUID professionalId,
        UUID serviceId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}