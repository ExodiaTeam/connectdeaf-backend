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

    ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable("userId") UUID user_id) throws Exception;

    ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable("userId") UUID userId,
                                                       @RequestBody CustomerEntryDto customerEntryDto) throws Exception;

    ResponseEntity<String> deleteUser(@PathVariable("userId") UUID userId);
}
