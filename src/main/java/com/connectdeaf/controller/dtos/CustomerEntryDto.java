package com.connectdeaf.controller.dtos;


public record CustomerEntryDto(
        String name,
        String email,
        String password,
        String numberPhone
) {
}
