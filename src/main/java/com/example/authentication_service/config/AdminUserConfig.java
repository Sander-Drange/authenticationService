package com.example.authentication_service.config;

import com.example.authentication_service.entity.Role;
import com.example.authentication_service.entity.User;
import com.example.authentication_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserConfig {

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@example.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User adminUser = User.builder()
                        .firstname("Admin")
                        .lastname("User")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("admin")) // Hardcoded password, consider changing for production
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(adminUser);
                System.out.println("Admin user created: " + adminEmail);
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
