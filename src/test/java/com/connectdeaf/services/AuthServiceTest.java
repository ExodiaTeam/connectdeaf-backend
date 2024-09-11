package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.LoginRequestDTO;
import com.connectdeaf.controllers.dtos.response.LoginResponseDTO;
import com.connectdeaf.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService mockUserService;
    @Mock
    private JwtEncoder mockJwtEncoder;
    @Mock
    private BCryptPasswordEncoder mockBCryptPasswordEncoder;

    private AuthService authServiceUnderTest;

    @BeforeEach
    void setUp() {
        //authServiceUnderTest = new AuthService(mockUserService, mockJwtEncoder, mockBCryptPasswordEncoder);
    }

    @Test
    void testLogin() {
        // Setup
        final LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                "alice.johnson@example.com",
                "password123"
        );

        final LoginResponseDTO expectedResult = new LoginResponseDTO("tokenValue", 300L);

        // Configure UserService.findUserByEmail(...).
        final Optional<User> userOptional = Optional.of(
                new User(
                        "Alice Johnson",
                        "alice.johnson@example.com",
                        "$2a$10$hashOfPassword",
                        "+1122334455"
                )
        );
        when(mockUserService.findUserByEmail("alice.johnson@example.com")).thenReturn(userOptional);

        when(mockBCryptPasswordEncoder.matches("password123", "$2a$10$hashOfPassword")).thenReturn(true);

        // Configure JwtEncoder.encode(...).
        final Instant issuedAt = LocalDateTime.of(
                2023,
                8,
                31,
                0, 0,
                0,
                0
        ).toInstant(ZoneOffset.UTC);
        final Instant expiresAt = issuedAt.plusSeconds(300L);
        final Jwt jwt = new Jwt(
                "tokenValue",
                issuedAt,
                expiresAt,
                Map.ofEntries(Map.entry("claim", "value")),
                Map.ofEntries(Map.entry("claim", "value"))
        );

        when(mockJwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Run the test
        final LoginResponseDTO result = authServiceUnderTest.login(loginRequestDTO);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testLogin_UserServiceReturnsAbsent() {
        final LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                "alice.johnson@example.com",
                "password123"
        );
        when(mockUserService.findUserByEmail("alice.johnson@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authServiceUnderTest.login(loginRequestDTO))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void testLogin_BCryptPasswordEncoderReturnsFalse() {
        final LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                "alice.johnson@example.com",
                "password123"
        );

        // Configure UserService.findUserByEmail(...).
        final Optional<User> userOptional = Optional.of(
                new User(
                        "Alice Johnson",
                        "alice.johnson@example.com",
                        "$2a$10$hashOfPassword",
                        "+1122334455"
                )
        );
        when(mockUserService.findUserByEmail("alice.johnson@example.com")).thenReturn(userOptional);

        when(mockBCryptPasswordEncoder.matches(
                "password123",
                "$2a$10$hashOfPassword"
        )).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> authServiceUnderTest.login(loginRequestDTO))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void testLogin_JwtEncoderThrowsJwtEncodingException() {
        // Setup
        final LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                "alice.johnson@example.com",
                "password123"
        );

        // Configure UserService.findUserByEmail(...).
        final Optional<User> userOptional = Optional.of(
                new User(
                        "Alice Johnson",
                        "alice.johnson@example.com",
                        "$2a$10$hashOfPassword",
                        "+1122334455"
                )
        );
        when(mockUserService.findUserByEmail("alice.johnson@example.com")).thenReturn(userOptional);

        when(mockBCryptPasswordEncoder.matches(
                "password123",
                "$2a$10$hashOfPassword"
        )).thenReturn(true);

        when(mockJwtEncoder.encode(any(JwtEncoderParameters.class))).thenThrow(JwtEncodingException.class);

        // Run the test
        assertThatThrownBy(() -> authServiceUnderTest.login(loginRequestDTO)).isInstanceOf(JwtEncodingException.class);
    }
}
