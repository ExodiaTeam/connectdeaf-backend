package com.connectdeaf.controller.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomerEntryDto {
    @NotBlank(message = "O nome não pode estar em branco")
    private String name;

    @Email(message = "O email deve ser válido")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco")
    @Pattern.List({@Pattern(regexp = "^(?=.*[0-9]).{8,}$", message = "A senha deve conter pelo menos um número"),
            @Pattern(regexp = "^(?=.*[a-z]).{8,}$", message = "A senha deve conter pelo menos uma letra minúscula"),
            @Pattern(regexp = "^(?=.*[A-Z]).{8,}$", message = "A senha deve conter pelo menos uma letra maiúscula"),
            @Pattern(regexp = "^(?=.*[@#$%^&+=]).{8,}$", message = "A senha deve conter pelo menos um caractere especial"),
            @Pattern(regexp = "\\S+", message = "A senha não pode conter espaços em branco")})
    @Size(min = 8)
    private String password;

    @NotBlank(message = "O número de telefone não pode estar em branco")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9\\d{4}-\\d{4}$", message = "O número de telefone deve ser válido")
    private String numberPhone;
}
