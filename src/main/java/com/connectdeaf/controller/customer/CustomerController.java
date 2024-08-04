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
    public ResponseEntity<CustomerResponseDto> saveCustomer(@RequestBody CustomerEntryDto customerEntryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.saveCustomer(customerEntryDto));
    }

    @Override
    @GetMapping(path = "/{userId}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable(("userId")) UUID userId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findById(userId));
    }

    @Override
    @PutMapping(path = "/{userId}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable("userId") UUID userId,
                                                              @RequestBody CustomerEntryDto customerEntryDto) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateUser(userId, customerEntryDto));
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.deleteUser(userId));
    }
}
