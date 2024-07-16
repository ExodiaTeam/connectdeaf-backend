package com.connectdeaf.controller;

import com.connectdeaf.controller.dtos.CustomerEntryDto;
import com.connectdeaf.controller.dtos.CustomerResponseDto;
import com.connectdeaf.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerEntryDto customerEntryDto;

    @BeforeEach
    public void setup() {
        customerEntryDto = new CustomerEntryDto();
        customerEntryDto.setName("John Doe");
        customerEntryDto.setEmail("john.doe@example.com");
        customerEntryDto.setPassword("Password1@");
        customerEntryDto.setNumberPhone("(11) 91234-5678");
    }

    @Test
    public void whenValidInputCustomer() throws Exception {
        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(UUID.randomUUID());

        when(customerService.saverCustomer(customerEntryDto)).thenReturn(customerResponseDto);

        mockMvc.perform(post("/api/customer/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerEntryDto)))
                .andExpect(status().isCreated());
    }
}
