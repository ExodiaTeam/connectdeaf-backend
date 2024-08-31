package com.connectdeaf.controllers;

import com.connectdeaf.controllers.dtos.requests.AddressRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService mockUserService;

    @Test
    void testCreateUser() throws Exception {
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "+1234567890",
                List.of(new AddressRequestDTO(
                        "12345-678",
                        "Main St",
                        "123",
                        "Apt 101",
                        "Downtown",
                        "Springfield",
                        "IL"))
        );

        final UserResponseDTO userResponseDTO = new UserResponseDTO(
                UUID.fromString("9e28442e-0258-485b-aeee-3a056f4e2357"),
                "John Doe", "john.doe@example.com",
                "+1234567890"
        );

        when(mockUserService.createUser(any(UserRequestDTO.class)))
                .thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTO)));

        verify(mockUserService).createUser(any(UserRequestDTO.class));
    }

    @Test
    void testGetUser() throws Exception {
        final UUID userId = UUID.fromString("86f03304-71a6-4596-b223-48667365c34f");

        final UserResponseDTO userResponseDTO = new UserResponseDTO(
                userId, "Alice Johnson", "alice.johnson@example.com", "+1122334455"
        );

        when(mockUserService.createUserDTO(eq(userId))).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/{user_id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTO)));

        verify(mockUserService).createUserDTO(eq(userId));
    }

    @Test
    void testGetAllUsers() throws Exception {
        final List<UserResponseDTO> userResponseDTOList = List.of(
                new UserResponseDTO(UUID.fromString(
                        "91318e88-378a-4c56-bf95-2bcd8628c946"),
                        "Bob Brown", "bob.brown@example.com",
                        "+1234567890"),
                new UserResponseDTO(UUID.fromString(
                        "e181d3ae-7526-4fa8-bdd9-1f760f0f4f39"),
                        "Charlie Green", "charlie.green@example.com",
                        "+0987654321")
        );

        when(mockUserService.findAllUsers()).thenReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTOList)));

        verify(mockUserService).findAllUsers();
    }

    @Test
    void testGetAllUsers_UserServiceReturnsNoItems() throws Exception {
        // Configura o mock
        when(mockUserService.findAllUsers()).thenReturn(Collections.emptyList());

        // Executa a requisição GET
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        // Verifica se o serviço foi chamado corretamente
        verify(mockUserService).findAllUsers();
    }

    @Test
    void testDeleteUser() throws Exception {
        final UUID userId = UUID.fromString("e5614a68-8937-4321-b25b-a694d4303385");

        mockMvc.perform(delete("/api/users/{user_id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mockUserService).deleteUser(eq(userId));
    }
}
