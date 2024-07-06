package com.connectdeaf.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CustomerResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String numberPhone;
}
