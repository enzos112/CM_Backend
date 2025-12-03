package com.medico.backend.controller;

import com.medico.backend.dto.request.CitaRequest;
import com.medico.backend.dto.response.CitaResponse;
import com.medico.backend.model.Cita;
import com.medico.backend.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping("/agendar")
    public ResponseEntity<CitaResponse> agendarCita(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.agendarCita(request));
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<List<Cita>> listarMisCitas() {
        return ResponseEntity.ok(citaService.listarMisCitas());
    }
}