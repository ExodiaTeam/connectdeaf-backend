package com.connectdeaf.controllers.dtos.requests;


import java.util.List;

public record ProfessionalRequestDTO(
        String name,
        String email,
        String password,
        String phoneNumber,
        List<AddressRequestDTO> addresses,
        String qualification,
        String areaOfExpertise
) {
}
