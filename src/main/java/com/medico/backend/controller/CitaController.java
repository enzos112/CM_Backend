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

    // --- NUEVO ENDPOINT PARA LA GRILLA DE HORARIOS ---
    @GetMapping("/horarios-ocupados")
    public ResponseEntity<List<String>> obtenerHorariosOcupados(
            @RequestParam Integer medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // 1. Definimos el rango del día completo (00:00 a 23:59)
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        // 2. Consultamos a la BD
        List<LocalDateTime> citas = citaRepository.findHorasOcupadas(medicoId, inicio, fin);

        // 3. Convertimos las fechas completas a solo hora "HH:mm" (Ej: "10:30")
        // Esto facilita la comparación en el Frontend
        List<String> horasOcupadas = citas.stream()
                .map(fechaHora -> fechaHora.toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toList());

        return ResponseEntity.ok(horasOcupadas);
    }
}