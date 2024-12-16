package com.example.authentication_service.controller;

import com.example.authentication_service.dto.request.AuthenticationRequest;
import com.example.authentication_service.dto.response.AuthenticationResponse;
import com.example.authentication_service.dto.request.RegisterRequest;
import com.example.authentication_service.entity.User;
import com.example.authentication_service.repository.UserRepository;
import com.example.authentication_service.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/secure-data")
    public String getSecuredData() {
        return "This is secured data. You have successfully accessed it!";
    }
}
