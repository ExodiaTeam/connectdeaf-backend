package com.connectdeaf.controller.customer;

import com.connectdeaf.controller.dtos.CustomerEntryDto;
import com.connectdeaf.controller.dtos.CustomerResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface IControllerCustomer {
    ResponseEntity<CustomerResponseDto> saveCustomer(@RequestBody @Valid CustomerEntryDto customerEntryDto);

    ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable UUID user_id) throws Exception;
}
