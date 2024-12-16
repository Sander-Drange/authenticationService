package com.example.authentication_service.service;

import com.example.authentication_service.config.JwtService;
import com.example.authentication_service.dto.request.AuthenticationRequest;
import com.example.authentication_service.dto.response.AuthenticationResponse;
import com.example.authentication_service.dto.request.RegisterRequest;
import com.example.authentication_service.dto.events.UserRegisteredEvent;
import com.example.authentication_service.entity.Role;
import com.example.authentication_service.entity.User;
import com.example.authentication_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RabbitTemplate rabbitTemplate;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role
                .build();

        var savedUser = repository.save(user);

        // Generate token with role
        var jwtToken = jwtService.generateToken(savedUser.getId(), savedUser.getRole().name());

        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                savedUser.getFirstname(),
                savedUser.getLastname()
        );
        rabbitTemplate.convertAndSend("user.exchange", "user.registration", event);
        log.info("User registration event published successfully");

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate token with role
        var jwtToken = jwtService.generateToken(user.getId(), user.getRole().name());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
