package com.connectdeaf.controller.customer;

import com.connectdeaf.controller.dtos.CustomerEntryDto;
import com.connectdeaf.controller.dtos.CustomerResponseDto;
import com.connectdeaf.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController implements IControllerCustomer {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @PostMapping(path = "/")
    public ResponseEntity<CustomerResponseDto> saveCustomer(CustomerEntryDto customerEntryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saveCustomer(customerEntryDto));
    }

    @Override
    @GetMapping(path = "/{userId}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable UUID userId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findById(userId));
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable UUID userId,
                                                              CustomerEntryDto customerEntryDto) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateUser(userId, customerEntryDto));
    }
}
