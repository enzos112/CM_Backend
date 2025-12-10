package com.medico.backend.controller;

import com.medico.backend.dto.response.AuthResponse;
import com.medico.backend.dto.request.LoginRequest;
import com.medico.backend.dto.request.RegisterRequest;
import com.medico.backend.service.implementation.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) { // <--- AQUÍ
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) { // <--- Y AQUÍ
        return ResponseEntity.ok(authService.login(request));
    }
}