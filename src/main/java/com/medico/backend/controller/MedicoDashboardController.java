package com.medico.backend.controller;

import com.medico.backend.model.core.Persona;
import com.medico.backend.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/intranet/medico")
@RequiredArgsConstructor
public class MedicoDashboardController {

    private final CitaRepository citaRepository;

    @GetMapping("/agenda-hoy")
    public List<Map<String, Object>> agendaHoy(Authentication auth) {

        String email = auth.getName();  // email del usuario logueado
        LocalDate hoy = LocalDate.now();

        var citas = citaRepository.findByMedicoUsuarioEmailAndFecha(email, hoy);

        return citas.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();

            // Hora de la cita
            map.put("hora", c.getFechaHoraInicio().toLocalTime().toString());

            // Nombre completo del paciente
            Persona p = c.getPaciente();
            String nombreCompleto = p.getNombres() + " " +
                    p.getApellidoPaterno() + " " +
                    (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "");
            map.put("paciente", nombreCompleto.trim());

            // Estado
            map.put("estado", c.getEstado());

            return map;
        }).toList();
    }

    @PatchMapping("/cita/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Integer id, @RequestParam String estado) {

        var cita = citaRepository.findById(id).orElseThrow();

        cita.setEstado(estado);
        citaRepository.save(cita);

        return ResponseEntity.ok("Estado actualizado correctamente");
    }
}

