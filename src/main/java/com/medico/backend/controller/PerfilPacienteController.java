package com.medico.backend.controller;

import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.core.Persona;
import com.medico.backend.repository.CitaRepository;
import com.medico.backend.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <--- FALTABA ESTO

@RestController
@RequestMapping("/web")
@RequiredArgsConstructor
public class PerfilPacienteController {

    private final PersonaRepository personaRepository;
    private final CitaRepository citaRepository;

    @PutMapping("/perfil")
    public Persona actualizarPerfil(Authentication auth, @RequestBody Persona datos) {

        String email = auth.getName();
        Persona persona = personaRepository.findByUsuarioEmail(email)
                .orElseThrow();

        persona.setTelefonoMovil(datos.getTelefonoMovil());
        persona.setDireccionCalle(datos.getDireccionCalle());

        return personaRepository.save(persona);
    }

    @GetMapping("/historial")
    public List<Cita> historial(Authentication auth) {
        String email = auth.getName();

        return citaRepository.findByPacienteUsuarioEmail(email);
    }
}