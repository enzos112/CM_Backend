package com.medico.backend.controller;

import com.medico.backend.dto.request.CitaRequest;
import com.medico.backend.dto.response.CitaResponse;
import com.medico.backend.repository.CitaRepository;
import com.medico.backend.service.implementation.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final CitaRepository citaRepository;

    @PostMapping("/agendar")
    public ResponseEntity<CitaResponse> agendar(@RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.agendarCita(request));
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<List<CitaResponse>> misCitas() {
        return ResponseEntity.ok(citaService.listarMisCitas());
    }

    @GetMapping("/agenda-medico")
    public ResponseEntity<List<CitaResponse>> verAgendaMedico(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (fecha == null) fecha = LocalDate.now();
        return ResponseEntity.ok(citaService.listarAgendaMedico(fecha));
    }

    @GetMapping("/horarios-ocupados")
    public ResponseEntity<List<String>> obtenerHorariosOcupados(
            @RequestParam Integer medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        List<LocalDateTime> citas = citaRepository.findHorasOcupadas(medicoId, inicio, fin);
        List<String> horasOcupadas = citas.stream()
                .map(fechaHora -> fechaHora.toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toList());
        return ResponseEntity.ok(horasOcupadas);
    }

}