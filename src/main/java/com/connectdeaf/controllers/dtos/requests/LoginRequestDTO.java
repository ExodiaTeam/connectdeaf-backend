package com.connectdeaf.controllers.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @Valid @Email String email,
        @Valid @NotBlank String password
) {
}
