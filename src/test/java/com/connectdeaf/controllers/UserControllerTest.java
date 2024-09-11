package com.connectdeaf.controllers;

import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "John Doe", "john.doe@example.com", "password123", "+1234567890", new ArrayList<>()
        );
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                userId, "John Doe", "john.doe@example.com", "+1234567890"
        );

        // Mockando o método createUser para retornar um UserResponseDTO
        when(userService.createUser(any(UserRequestDTO.class), eq(false)))
                .thenReturn(userResponseDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"John Doe\", " +
                                "\"email\": \"john.doe@example.com\", " +
                                "\"password\": \"password123\", " +
                                "\"phoneNumber\": \"+1234567890\", " +
                                "\"addresses\": [] }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+1234567890"));
    }

    // Teste para buscar um usuário com sucesso
    @Test
    void testGetUser() throws Exception {
        UUID userId = UUID.fromString("86f03304-71a6-4596-b223-48667365c34f");

        // Criação do User para ser retornado pelo mock
        User user = new User();
        user.setId(userId);
        user.setName("Alice Johnson");
        user.setEmail("alice.johnson@example.com");
        user.setPhoneNumber("+1122334455");

        // Configurando o mock para retornar o User
        when(mockUserService.findById(eq(userId))).thenReturn(user);

        // Esperado UserResponseDTO para comparação
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                userId, "Alice Johnson", "alice.johnson@example.com", "+1122334455"
        );

        mockMvc.perform(get("/api/users/{user_id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userResponseDTO)));

        verify(mockUserService).findById(eq(userId));
    }

    // Teste para buscar um usuário que não existe
    @Test
    void testGetUser_NotFound() throws Exception {
        UUID userId = UUID.fromString("86f03304-71a6-4596-b223-48667365c34f");

        when(mockUserService.findById(eq(userId)))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/users/{user_id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(mockUserService).findById(eq(userId));
    }

    // Teste para atualizar um usuário com sucesso
    @Test
    void testUpdateUser() throws Exception {
        UUID userId = UUID.fromString("86f03304-71a6-4596-b223-48667365c34f");

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "+1234567890",
                Collections.emptyList()
        );

        UserResponseDTO updatedUser = new UserResponseDTO(
                userId, "John Doe", "john.doe@example.com", "+1234567890"
        );

        when(mockUserService.updateUser(eq(userId), any(UserRequestDTO.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{user_id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedUser)));

        verify(mockUserService).updateUser(eq(userId), any(UserRequestDTO.class));
    }

    // Teste para deletar um usuário com sucesso
    @Test
    void testDeleteUser() throws Exception {
        UUID userId = UUID.fromString("e5614a68-8937-4321-b25b-a694d4303385");

        doNothing().when(mockUserService).deleteUser(eq(userId));

        mockMvc.perform(delete("/api/users/{user_id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mockUserService).deleteUser(eq(userId));
    }

    // Teste para deletar um usuário que não existe
    @Test
    void testDeleteUser_NotFound() throws Exception {
        UUID userId = UUID.fromString("e5614a68-8937-4321-b25b-a694d4303385");

        doThrow(new UserNotFoundException())
                .when(mockUserService).deleteUser(eq(userId));

        mockMvc.perform(delete("/api/users/{user_id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(mockUserService).deleteUser(eq(userId));
    }
}
