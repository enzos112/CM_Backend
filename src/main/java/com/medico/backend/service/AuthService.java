package com.medico.backend.service;

import com.medico.backend.config.security.JwtService;
import com.medico.backend.dto.response.AuthResponse;
import com.medico.backend.dto.request.LoginRequest;
import com.medico.backend.dto.request.RegisterRequest;
import com.medico.backend.model.security.Rol;
import com.medico.backend.model.core.Usuario;
import com.medico.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Creamos el usuario con los datos del formulario
        Usuario user = new Usuario();
        user.setNombres(request.getNombres());
        user.setApellidoPaterno(request.getApellidoPaterno());
        user.setApellidoMaterno(request.getApellidoMaterno());
        user.setEmail(request.getEmail());
        user.setTelefono(request.getTelefono());
        user.setNumeroDocumento(request.getDni());
        user.setTipoDocumento("DNI"); // Por defecto

        // ¡IMPORTANTE! Encriptamos la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRol(Rol.PACIENTE); // Por defecto todos son pacientes al registrarse

        usuarioRepository.save(user);

        // Generamos el token automáticamente para que quede logueado
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Esto autentica contra Spring Security (si falla lanza excepción automáticamente)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si llegamos aquí, las credenciales son correctas. Buscamos al usuario y damos token.
        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}