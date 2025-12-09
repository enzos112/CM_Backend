package com.medico.backend.controller;

import com.medico.backend.model.infrastructure.HorarioMedico;
import com.medico.backend.service.implementation.HorarioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/intranet/medico/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;

    // DTO estática para recibir datos limpios del JSON
    @Data
    static class HorarioRequest {
        private Integer diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private Integer idModalidad;
    }

    @PostMapping
    public ResponseEntity<?> crear(Authentication auth, @RequestBody HorarioRequest req) {
        try {
            String email = auth.getName();
            HorarioMedico nuevo = horarioService.registrarHorario(
                    email,
                    req.getDiaSemana(),
                    req.getHoraInicio(),
                    req.getHoraFin(),
                    req.getIdModalidad()
            );
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<HorarioMedico> listar(Authentication auth) {
        return horarioService.listarMisHorarios(auth.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        horarioService.eliminarHorario(id);
        return ResponseEntity.ok("Horario eliminado");
    }
}