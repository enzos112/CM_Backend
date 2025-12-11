package com.medico.backend.controller;

import com.medico.backend.dto.request.ChangeEmailRequest;
import com.medico.backend.dto.request.ChangePasswordRequest;
import com.medico.backend.dto.request.UpdateProfileRequest;
import com.medico.backend.dto.response.AuthResponse;
import com.medico.backend.service.implementation.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario") // <--- ESTO define la ruta base
@RequiredArgsConstructor
public class UsuarioController {

    private final AuthService authService;

    // 1. Actualizar Datos Personales
    @PutMapping("/perfil")
    public ResponseEntity<AuthResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }

    // DEBE SER ASÍ:
    @PutMapping("/change-password") // <--- Verifica que diga PutMapping
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // 3. Cambiar Correo (Este es el que te daba 404)
    @PutMapping("/change-email")
    public ResponseEntity<AuthResponse> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        return ResponseEntity.ok(authService.changeEmail(request));
    }
}