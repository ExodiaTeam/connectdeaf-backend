package com.connectdeaf.infrastructure.controllers.dtos.requests;

import java.util.UUID;

record AddressRequestDTO(
        String cep,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state
) {
}
