package com.connectdeaf.controller.customer;

import com.connectdeaf.controller.dtos.CustomerEntryDto;
import com.connectdeaf.controller.dtos.CustomerResponseDto;
import com.connectdeaf.repository.CustomerRepository;
import com.connectdeaf.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class CustomerController implements IControllerCustomer {
    private final CustomerService customerService;

    public CustomerController(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    @PostMapping(path = "/")
    public ResponseEntity<CustomerResponseDto> saveCustomer(CustomerEntryDto customerEntryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saverCustomer(customerEntryDto));
    }

    @Override
    @GetMapping(path = "/{user_id}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable UUID user_id) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findById(user_id));
    }
}
