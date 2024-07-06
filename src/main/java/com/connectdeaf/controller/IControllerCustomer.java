package com.connectdeaf.controller;

import com.connectdeaf.dtos.CustomerEntryDto;
import com.connectdeaf.dtos.CustomerResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

public interface IControllerCustomer {
    ResponseEntity<CustomerResponseDto> saveCustomer(@RequestBody @Valid CustomerEntryDto customerEntryDto);
}
