package com.connectdeaf.services;

import com.connectdeaf.dtos.CustomerEntryDto;
import com.connectdeaf.dtos.CustomerResponseDto;
import com.connectdeaf.exceptions.EmailAlreadyExistsException;
import com.connectdeaf.model.CustomerModel;
import com.connectdeaf.repository.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDto saverCustomer(CustomerEntryDto customerEntryDto) {
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
}
