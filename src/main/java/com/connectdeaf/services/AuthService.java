package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.LoginRequestDTO;
import com.connectdeaf.controllers.dtos.response.LoginResponseDTO;
import com.connectdeaf.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserService userService, JwtEncoder jwtEncoder, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.jwtEncoder = jwtEncoder;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        logger.info("Tentativa de login para o email: {}", loginRequestDTO.email());

        try {
            User user = userService.findUserByEmail(loginRequestDTO.email())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            if (!bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
                logger.warn("Senha inválida para o email: {}", loginRequestDTO.email());
                throw new BadCredentialsException("Email ou senha inválidos");
            }

            var now = Instant.now();
            var expiresIn = 3600L; // Aumentado para 1 hora

            var claims = JwtClaimsSet.builder()
                    .issuer("connect-deaf-app")
                    .subject(user.getId().toString())
                    .expiresAt(now.plusSeconds(expiresIn))
                    .issuedAt(now)
                    .claim("email", user.getEmail())
                    .build();

            var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            logger.info("Login bem-sucedido para o email: {}", loginRequestDTO.email());
            return new LoginResponseDTO(jwtValue, expiresIn);
        } catch (Exception e) {
            logger.error("Erro durante o login: ", e);
            throw e;
        }
    }
}
