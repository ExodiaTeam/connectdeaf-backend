package com.connectdeaf.controllers.dtos.response;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

public record ProfessionalResponseDTO(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        String qualification,
        String areaOfExpertise,
        LocalTime workStartTime,  
        LocalTime workEndTime,    
        Duration breakDuration    
) {
}

