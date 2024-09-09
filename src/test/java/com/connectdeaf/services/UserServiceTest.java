package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.AddressRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.address.Address;
import com.connectdeaf.domain.user.Role;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.EmailAlreadyExistsException;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.repositories.RoleRepository;
import com.connectdeaf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private AddressService mockAddressService;
    @Mock
    private BCryptPasswordEncoder mockPasswordEncoder;
    @Mock
    private RoleRepository mockRoleRepository;

    private UserService userServiceUnderTest;

    @BeforeEach
    void setUp() {
        userServiceUnderTest = new UserService(mockUserRepository, mockAddressService, mockPasswordEncoder,
                mockRoleRepository);
    }

    @Test
    void testCreateUser_ThrowsEmailAlreadyExistsException() {
        // Setup
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "securepassword123",
                "+1-555-1234",
                List.of(new AddressRequestDTO(
                        "90210",
                        "Beverly Hills Blvd",
                        "123",
                        "Suite 101",
                        "Beverly Hills",
                        "Los Angeles",
                        "CA")));

        // Configure UserRepository.findByEmail(...).
        final Optional<User> userOptional = Optional.of(new User("John Doe", "john.doe@example.com", "securepassword123", "+1-555-1234"));
        when(mockUserRepository.findByEmail("john.doe@example.com")).thenReturn(userOptional);

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.createUser(userRequestDTO, false))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void testCreateUser_UserRepositoryFindByEmailReturnsAbsent() {
        // Setup
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Jane Smith",
                "jane.smith@example.com",
                "mypassword456",
                "+1-555-5678",
                List.of(new AddressRequestDTO(
                        "10001",
                        "5th Avenue",
                        "789",
                        "Apt 10B",
                        "Manhattan",
                        "New York",
                        "NY")));

        final UserResponseDTO expectedResult = new UserResponseDTO(
                UUID.fromString("a7b8c9d0-e1f2-3456-789a-bcde12345678"),
                "Jane Smith",
                "jane.smith@example.com",
                "+1-555-5678");

        when(mockUserRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());

        // Configure RoleRepository.findByName(...)
        final Role role = new Role();
        when(mockRoleRepository.findByName(Role.Values.ROLE_USER.name())).thenReturn(role);

        when(mockPasswordEncoder.encode("mypassword456")).thenReturn("encryptedpassword");

        // Configure UserRepository.save(...)
        final User user = new User("Jane Smith", "jane.smith@example.com", "encryptedpassword", "+1-555-5678");
        user.setId(UUID.fromString("a7b8c9d0-e1f2-3456-789a-bcde12345678"));

        when(mockUserRepository.save(any(User.class))).thenReturn(user);

        // Run the test
        final UserResponseDTO result = userServiceUnderTest.createUser(userRequestDTO, false);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockAddressService).saveAddress(any(Address.class));
    }

    @Test
    void testCreateUserDTO_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepository.findById(UUID.fromString("b8c9d0e1-f2a3-4567-89ab-cde123456789")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.createUserDTO(
                UUID.fromString("b8c9d0e1-f2a3-4567-89ab-cde123456789"))).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testFindById() {
        // Setup
        final UUID userId = UUID.fromString("f7c4d6e2-8a3b-4c1f-b5a7-789bcd12ef34");
        final Optional<User> userOptional = Optional.of(new User("Alice Johnson", "alice.johnson@example.com", "password789", "+1-555-8765"));
        when(mockUserRepository.findById(userId)).thenReturn(userOptional);

        // Run the test
        final User result = userServiceUnderTest.findById(userId);

        // Verify the results
        assertThat(result.getName()).isEqualTo("Alice Johnson");
        assertThat(result.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+1-555-8765");
    }

    @Test
    void testFindById_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepository.findById(UUID.fromString("f7c4d6e2-8a3b-4c1f-b5a7-789bcd12ef34")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.findById(
                UUID.fromString("f7c4d6e2-8a3b-4c1f-b5a7-789bcd12ef34"))).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testFindAllUsers() {
        // Setup
        final UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final List<UserResponseDTO> expectedResult = List.of(
                new UserResponseDTO(userId, "Bob Brown", "bob.brown@example.com", "+1-555-1111"));

        // Configure UserRepository.findAll(...).
        final User user = new User("Bob Brown", "bob.brown@example.com", "password123", "+1-555-1111");
        user.setId(userId);
        when(mockUserRepository.findAll()).thenReturn(List.of(user));

        // Run the test
        final List<UserResponseDTO> result = userServiceUnderTest.findAllUsers();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAllUsers_UserRepositoryReturnsNoItems() {
        // Setup
        when(mockUserRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<UserResponseDTO> result = userServiceUnderTest.findAllUsers();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testDeleteUser() {
        // Setup
        final UUID userId = UUID.fromString("c9b1d7d4-5b2e-4f3a-bf43-2d1234567890");
        final Optional<User> userOptional = Optional.of(new User("Emma Davis", "emma.davis@example.com", "password456", "+1-555-2222"));
        when(mockUserRepository.findById(userId)).thenReturn(userOptional);

        // Run the test
        userServiceUnderTest.deleteUser(userId);

        // Verify the results
        verify(mockUserRepository).delete(any(User.class));
    }

    @Test
    void testDeleteUser_UserRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockUserRepository.findById(UUID.fromString("c9b1d7d4-5b2e-4f3a-bf43-2d1234567890")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.deleteUser(
                UUID.fromString("c9b1d7d4-5b2e-4f3a-bf43-2d1234567890"))).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testUpdateUser() {
        // Setup
        final UUID userId = UUID.fromString("b9b9c9c9-d8d8-4a4a-bb9b-8b9b9b9b9b9b");
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Michael Green",
                "michael.green@example.com",
                "newpassword123",
                "+1-555-3333",
                List.of(new AddressRequestDTO(
                        "60606",
                        "State St",
                        "101",
                        "Apt 2A",
                        "Chicago",
                        "Cook",
                        "IL")));

        final User existingUser = new User("Michael Green", "michael.green@example.com", "oldpassword", "+1-555-3333");
        existingUser.setId(userId);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Configure UserRepository.save(...)
        final User updatedUser = new User("Michael Green", "michael.green@example.com", "newencryptedpassword", "+1-555-3333");
        updatedUser.setId(userId);

        when(mockUserRepository.save(any(User.class))).thenReturn(updatedUser);

        // Run the test
        final UserResponseDTO result = userServiceUnderTest.updateUser(userId, userRequestDTO);

        // Verify the results
        assertThat(result.name()).isEqualTo("Michael Green");
        assertThat(result.email()).isEqualTo("michael.green@example.com");

        // Verify that the mocks were used as expected
        verify(mockUserRepository).findById(userId);
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserRepositoryFindByIdReturnsAbsent() {
        // Setup
        final UUID userId = UUID.fromString("b9b9c9c9-d8d8-4a4a-bb9b-8b9b9b9b9b9b");
        final UserRequestDTO userRequestDTO = new UserRequestDTO(
                "David Lee",
                "david.lee@example.com",
                "password987",
                "+1-555-4444",
                List.of(new AddressRequestDTO(
                        "30303",
                        "Lake Shore Dr",
                        "505",
                        "Unit 3C",
                        "Chicago",
                        "Cook",
                        "IL")));

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.updateUser(userId, userRequestDTO))
                .isInstanceOf(UserNotFoundException.class);
    }
}
