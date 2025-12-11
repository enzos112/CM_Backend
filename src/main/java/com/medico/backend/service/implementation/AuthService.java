package com.medico.backend.service.implementation;

import com.medico.backend.config.security.JwtService;
import com.medico.backend.dto.request.LoginRequest;
import com.medico.backend.dto.request.RegisterRequest;
import com.medico.backend.dto.request.UpdateProfileRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

    @Transactional
    public AuthResponse updateProfile(UpdateProfileRequest request) {
        // 1. Obtener el email del usuario autenticado desde el contexto de seguridad
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Buscar Usuario y Persona
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Persona persona = personaRepository.findByUsuario(user)
                .orElseThrow(() -> new RuntimeException("Persona asociada no encontrada"));

        // 3. Actualizar SOLO los campos permitidos (si vienen en el request)
        if (request.getTelefonoMovil() != null) {
            persona.setTelefonoMovil(request.getTelefonoMovil());
        }
        if (request.getDireccionCalle() != null) {
            persona.setDireccionCalle(request.getDireccionCalle());
        }
        if (request.getContactoEmergenciaNombre() != null) {
            persona.setContactoEmergenciaNombre(request.getContactoEmergenciaNombre());
        }
        if (request.getContactoEmergenciaTelefono() != null) {
            persona.setContactoEmergenciaTelefono(request.getContactoEmergenciaTelefono());
        }

        // 4. Guardar cambios en BD
        personaRepository.save(persona);

        // 5. ¡CRUCIAL! Generar un NUEVO TOKEN con los datos actualizados
        // (El JwtService buscará de nuevo a la persona y verá los cambios)
        var newToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(newToken).build();
    }

    // Añadir al final de AuthService.java
    @Transactional
    public void changePassword(String newPassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Encriptamos la nueva clave antes de guardarla
        user.setPassword(passwordEncoder.encode(newPassword));

        usuarioRepository.save(user);
    }

    // Añadir en AuthService.java

    @Transactional
    public AuthResponse changeEmail(com.medico.backend.dto.request.ChangeEmailRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Verificar que el nuevo email no esté ocupado por OTRA persona
        if (usuarioRepository.findByEmail(request.getNewEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso por otro usuario.");
        }

        Usuario user = usuarioRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Actualizar Email
        user.setEmail(request.getNewEmail());
        usuarioRepository.save(user);

        // 3. Generar NUEVO TOKEN (Fundamental porque el token anterior tenía el email viejo)
        var newToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(newToken).build();
    }
}