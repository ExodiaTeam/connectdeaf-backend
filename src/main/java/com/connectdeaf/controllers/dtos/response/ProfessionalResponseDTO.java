package com.connectdeaf.controllers.dtos.response;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

public record ProfessionalResponseDTO(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        String qualification,
        String areaOfExpertise,
        LocalTime workStartTime,  
        LocalTime workEndTime,    
        Duration breakDuration,
        List<AddressResponseDTO> addresses
) {
}

