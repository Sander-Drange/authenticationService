package com.example.authentication_service.controller;

import com.example.authentication_service.dto.AuthenticationRequest;
import com.example.authentication_service.dto.AuthenticationResponse;
import com.example.authentication_service.dto.RegisterRequest;
import com.example.authentication_service.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

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

    @GetMapping("/secure-data")
    public String getSecuredData() {
        return "This is secured data. You have successfully accessed it!";
    }
}
