package com.connectdeaf.services;

import com.connectdeaf.controller.dtos.CustomerEntryDto;
import com.connectdeaf.controller.dtos.CustomerResponseDto;
import com.connectdeaf.exceptions.CustomerNotFoundException;
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
            throw new EmailAlreadyExistsException("Email já existe: " + customer.getEmail());
        }

        CustomerModel savedCustomer = customerRepository.save(customer);
        return new CustomerResponseDto(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getEmail(),
                savedCustomer.getNumberPhone()
        );
    }

    public CustomerResponseDto findById(UUID userId) throws Exception {
        Optional<CustomerModel> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new Exception("Usuário não cadastrado.");
        }

        CustomerModel customerModel = customer.get();
        return new CustomerResponseDto(
                customerModel.getId(),
                customerModel.getName(),
                customerModel.getEmail(),
                customerModel.getNumberPhone()
        );
    }

    public CustomerResponseDto updateUser(UUID userId, CustomerEntryDto customerEntryDto) throws Exception {
        Optional<CustomerModel> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new Exception("Usuário não cadastrado.");
        }

        CustomerModel existingCustomer = customer.get();
        BeanUtils.copyProperties(customerEntryDto, existingCustomer, "id");

        CustomerModel updatedCustomer = customerRepository.save(existingCustomer);
        return new CustomerResponseDto(
                updatedCustomer.getId(),
                updatedCustomer.getName(),
                updatedCustomer.getEmail(),
                updatedCustomer.getNumberPhone()
        );
    }

    public String deleteUser(UUID userId) {
        Optional<CustomerModel> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException("Usuário não encontrado com o ID: " + userId);
        }

        customerRepository.delete(customer.get());

        return "Usuário deletado com sucesso!";
    }
}
