package com.connectdeaf.controllers.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleResponseDTO(
        UUID id,
        UUID professionalId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Boolean isAvailable
) {
}