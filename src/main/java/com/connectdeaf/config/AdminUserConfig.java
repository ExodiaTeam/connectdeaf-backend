package com.connectdeaf.config;

import com.connectdeaf.domain.user.Role;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.repositories.RoleRepository;
import com.connectdeaf.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName(Role.Values.ROLE_ADMIN.name());

        userRepository.findByEmail("admin@email.com").ifPresentOrElse(
                admin -> System.out.println("Admin user already exists"),
                () -> {
                    User adminUser = new User();
                    adminUser.setName("Admin");
                    adminUser.setEmail("admin@email.com");
                    adminUser.setPassword(passwordEncoder.encode("admin"));
                    adminUser.setRoles(Set.of(adminRole));
                    userRepository.save(adminUser);
                    System.out.println("Admin user created");
                }
        );
    }
}