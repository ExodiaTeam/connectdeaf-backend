package com.connectdeaf.services;

import com.connectdeaf.controller.dtos.CustomerEntryDto;
import com.connectdeaf.controller.dtos.CustomerResponseDto;
import com.connectdeaf.exceptions.EmailAlreadyExistsException;
import com.connectdeaf.model.CustomerModel;
import com.connectdeaf.repository.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDto saveCustomer(CustomerEntryDto customerEntryDto) {
        CustomerModel customer = new CustomerModel();
        BeanUtils.copyProperties(customerEntryDto, customer);

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email já existe " + customer.getEmail());
        }

        CustomerModel savedCustomer = customerRepository.save(customer);
        CustomerResponseDto responseDto = new CustomerResponseDto();
        BeanUtils.copyProperties(savedCustomer, responseDto);

        return responseDto;
    }

    public CustomerResponseDto findById(UUID userId) throws Exception {
        Optional<CustomerModel> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new Exception("Usuário não cadastrado.");
        }

        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        BeanUtils.copyProperties(customer.get(), customerResponseDto);

        return customerResponseDto;
    }

    public CustomerResponseDto updateUser(UUID userId, CustomerEntryDto customerEntryDto) {
        Optional<CustomerModel> customer = customerRepository.findById(userId);


    }
}
