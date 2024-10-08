package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.LoginRequestDTO;
import com.connectdeaf.controllers.dtos.response.LoginResponseDTO;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.user.Role;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.repositories.ProfessionalRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;
    private final ProfessionalRepository professionalRepository;
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserService userService, ProfessionalRepository professionalRepository, JwtEncoder jwtEncoder,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.professionalRepository = professionalRepository;
        this.jwtEncoder = jwtEncoder;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        logger.info("Tentativa de login para o email: {}", loginRequestDTO.email());

        User user = authenticateUser(loginRequestDTO);
        String jwtToken = generateJwtToken(user);

        logger.info("Login bem-sucedido para o email: {}", loginRequestDTO.email());
        return new LoginResponseDTO(jwtToken, 3600L);
    }

    private User authenticateUser(LoginRequestDTO loginRequestDTO) {
        User user = userService.findUserByEmail(loginRequestDTO.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            logger.warn("Senha inválida para o email: {}", loginRequestDTO.email());
            throw new BadCredentialsException("Email ou senha inválidos");
        }

        return user;
    }

    private String generateJwtToken(User user) {
        var now = Instant.now();
        var expiresIn = 3600L;

        var claimsBuilder = JwtClaimsSet.builder()
                .issuer("connect-deaf-app")
                .subject(user.getId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(now)
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        Optional<Professional> professionalOpt = professionalRepository.findByUser(user);
        professionalOpt.ifPresent(professional -> claimsBuilder.claim("professionalId", professional.getId().toString()));

        var claims = claimsBuilder.build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
