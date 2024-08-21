package com.connectdeaf.service;

import com.connectdeaf.controllers.dtos.requests.AddressRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.exceptions.EmailAlreadyExistsException;
import com.connectdeaf.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "securePassword123",
                "+123456789",
                List.of(new AddressRequestDTO(
                        "12345-678",
                        "123 Main St",
                        "101",
                        "Apt 12",
                        "Downtown",
                        "Springfield",
                        "IL"
                ))
        );

        final UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.name()).isEqualTo("John Doe");
        assertThat(createdUser.email()).isEqualTo("john.doe@example.com");

        assertThat(userRepository.findByEmail("john.doe@example.com")).isPresent();
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        final UserRequestDTO userRequestDTO1 = new UserRequestDTO(
                "Jane Doe",
                "jane.doe@example.com",
                "anotherSecurePassword123",
                "+987654321",
                List.of(new AddressRequestDTO(
                        "87654-321",
                        "456 Elm St",
                        "202",
                        "Suite B",
                        "Midtown",
                        "Shelbyville",
                        "IN"
                ))
        );
        userService.createUser(userRequestDTO1);

        final UserRequestDTO userRequestDTO2 = new UserRequestDTO(
                "Jane Smith",
                "jane.doe@example.com",
                "anotherPassword456",
                "+555123456",
                List.of(new AddressRequestDTO(
                        "65432-876",
                        "789 Oak St",
                        "303",
                        "Floor 3",
                        "Uptown",
                        "Capital City",
                        "CA"
                ))
        );

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(userRequestDTO2));
    }

    @Test
    void testFindUser() {
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Alice Johnson",
                "alice.johnson@example.com",
                "passwordAlice",
                "+1122334455",
                List.of(new AddressRequestDTO(
                        "11223-445",
                        "321 Cedar St",
                        "404",
                        "Penthouse",
                        "Lakeside",
                        "Ogdenville",
                        "UT"
                ))
        );
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        final UserResponseDTO foundUser = userService.findUser(createdUser.id());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.name()).isEqualTo("Alice Johnson");
        assertThat(foundUser.email()).isEqualTo("alice.johnson@example.com");
    }

    @Test
    void testDeleteUser() {
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Bob Brown",
                "bob.brown@example.com",
                "passwordBob",
                "+9988776655",
                List.of(new AddressRequestDTO(
                        "99887-665",
                        "654 Birch St",
                        "505",
                        "Suite 5",
                        "Riverside",
                        "North Haverbrook",
                        "OR"
                ))
        );
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        userService.deleteUser(createdUser.id());
        assertThat(userRepository.findById(createdUser.id())).isNotPresent();
    }
}
