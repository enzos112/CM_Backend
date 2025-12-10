package com.medico.backend.config.security;

import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.infrastructure.Especialidad;
import com.medico.backend.model.infrastructure.ModalidadCita;
import com.medico.backend.model.privat.Rol;
import com.medico.backend.model.privat.UsuarioRol;
import com.medico.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j // Activa logs
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final EspecialidadRepository especialidadRepository;
    private final ModalidadCitaRepository modalidadCitaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🚀 INICIANDO CARGA DE DATOS MAESTROS...");

        // 1. ROLES
        crearRolSiNoExiste("ADMIN", "Administrador del Sistema");
        crearRolSiNoExiste("MEDICO", "Personal Médico");
        crearRolSiNoExiste("PACIENTE", "Usuario Paciente");

        // 2. MODALIDADES
        crearModalidadSiNoExiste("PRESENCIAL", "Cita en consultorio físico");
        crearModalidadSiNoExiste("VIRTUAL", "Cita por videollamada");

        // 3. ESPECIALIDADES
        crearEspecialidadSiNoExiste("Medicina General", "Atención primaria");
        crearEspecialidadSiNoExiste("Cardiología", "Enfermedades del corazón");
        crearEspecialidadSiNoExiste("Pediatría", "Atención a niños");

        // 4. USUARIO ADMIN INICIAL
        if (usuarioRepository.findByEmail("admin@medico.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .codigo("ADM-001")
                    .email("admin@medico.com")
                    .password(passwordEncoder.encode("admin123"))
                    .estadoCuenta("ACTIVO")
                    .fechaRegistro(LocalDateTime.now())
                    .build();

            Usuario savedAdmin = usuarioRepository.save(admin);

            Rol rolAdmin = rolRepository.findByNombreRol("ADMIN").orElseThrow();

            UsuarioRol ur = UsuarioRol.builder()
                    .usuario(savedAdmin)
                    .rol(rolAdmin)
                    .fechaAsignacion(LocalDateTime.now())
                    .build();

            usuarioRolRepository.save(ur);
            log.info("✅ Usuario ADMIN creado: admin@medico.com / admin123");
        } else {
            log.info("👌 Usuario ADMIN ya existe. Omitiendo creación.");
        }

        log.info("🏁 CARGA DE DATOS COMPLETADA EXITOSAMENTE.");
    }

    private void crearRolSiNoExiste(String nombre, String descripcion) {
        if (rolRepository.findByNombreRol(nombre).isEmpty()) {
            rolRepository.save(Rol.builder().nombreRol(nombre).descripcion(descripcion).build());
        }
    }

    private void crearModalidadSiNoExiste(String nombre, String descripcion) {
        if (modalidadCitaRepository.findByNombre(nombre).isEmpty()) {
            modalidadCitaRepository.save(ModalidadCita.builder().nombre(nombre).descripcion(descripcion).activo(true).build());
        }
    }

    private void crearEspecialidadSiNoExiste(String nombre, String descripcion) {
        if (especialidadRepository.count() == 0) {
            especialidadRepository.save(Especialidad.builder().nombre(nombre).descripcion(descripcion).iconoUrl("default.png").build());
        }
    }
}