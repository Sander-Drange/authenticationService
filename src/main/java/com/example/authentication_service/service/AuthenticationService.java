package com.example.authentication_service.service;

import com.example.authentication_service.config.JwtService;
import com.example.authentication_service.dto.AuthenticationRequest;
import com.example.authentication_service.dto.AuthenticationResponse;
import com.example.authentication_service.dto.RegisterRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
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
                .role(Role.USER)
                .build();
        
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                savedUser.getFirstname(),
                savedUser.getLastname()
                );
        rabbitTemplate.convertAndSend(
          "user.exchange",
          "user.registration",
                event
        );
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
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
