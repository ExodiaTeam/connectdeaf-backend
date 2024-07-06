package com.connectdeaf.controller;

import com.connectdeaf.dtos.CustomerEntryDto;
import com.connectdeaf.dtos.CustomerResponseDto;
import com.connectdeaf.model.CustomerModel;
import com.connectdeaf.repository.CustomerRepository;
import com.connectdeaf.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController implements IControllerCustomer {
    private final CustomerService customerService;

    public CustomerController(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerService = customerService;
    }


    @Override
    @PostMapping(path = "/create")
    public ResponseEntity<CustomerResponseDto> saveCustomer(CustomerEntryDto customerEntryDto) {
        var customerModel = new CustomerModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saverCustomer(customerEntryDto));
    }
}
