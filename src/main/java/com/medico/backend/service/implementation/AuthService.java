package com.medico.backend.service.implementation;

import com.medico.backend.config.security.JwtService;
import com.medico.backend.dto.request.LoginRequest;
import com.medico.backend.dto.request.RegisterRequest;
import com.medico.backend.dto.response.AuthResponse;
import com.medico.backend.model.core.Persona;
import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.privat.Rol;
import com.medico.backend.model.privat.UsuarioRol;
import com.medico.backend.repository.PersonaRepository;
import com.medico.backend.repository.RolRepository;
import com.medico.backend.repository.UsuarioRepository;
import com.medico.backend.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Crear USUARIO
        Usuario user = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .estadoCuenta("ACTIVO")
                .build();

        Usuario savedUser = usuarioRepository.save(user);

        // 2. Asignar ROL
        Rol rolPaciente = rolRepository.findByNombreRol("PACIENTE")
                .orElseThrow(() -> new RuntimeException("Rol PACIENTE no encontrado"));

        UsuarioRol usuarioRol = UsuarioRol.builder()
                .usuario(savedUser)
                .rol(rolPaciente)
                .build();

        usuarioRolRepository.save(usuarioRol);

        // 3. Crear PERSONA (Mapeo 1:1)
        Persona persona = Persona.builder()
                .usuario(savedUser)
                .nombres(request.getNombres())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .telefonoMovil(request.getTelefonoMovil())

                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())

                // --- Mapeo de los 4 campos estructurados ---
                .region(request.getRegion())
                .provincia(request.getProvincia())
                .distrito(request.getDistrito())
                .direccionCalle(request.getDireccionCalle()) // Usa el nuevo campo 'direccionCalle'

                // Mapeo de Contacto de Emergencia
                .contactoEmergenciaNombre(request.getContactoEmergenciaNombre())
                .contactoEmergenciaTelefono(request.getContactoEmergenciaTelefono())
                .build();

        personaRepository.save(persona);

        // 4. Token
        var jwtToken = jwtService.generateToken(savedUser);

        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Autenticamos con Email
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Buscamos el usuario en BD
        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // 3. Generamos Token
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}