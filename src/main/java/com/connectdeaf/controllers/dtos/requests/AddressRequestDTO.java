    package com.connectdeaf.controllers.dtos.requests;


    public record AddressRequestDTO(
            String cep,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state
    ) {
    }
