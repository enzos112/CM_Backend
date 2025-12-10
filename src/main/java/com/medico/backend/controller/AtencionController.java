package com.medico.backend.controller;

import com.medico.backend.dto.request.AtencionRequest;
import com.medico.backend.model.clinical.Atencion;
import com.medico.backend.service.implementation.AtencionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intranet/medico/atencion")
@RequiredArgsConstructor
public class AtencionController {

    private final AtencionService atencionService;

    @PostMapping("/registrar")
    public ResponseEntity<Atencion> registrarAtencion(@RequestBody AtencionRequest request) {
        return ResponseEntity.ok(atencionService.registrarAtencion(request));
    }

    @GetMapping("/historial/{idPaciente}")
    public ResponseEntity<List<Atencion>> verHistorial(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(atencionService.obtenerHistorialPaciente(idPaciente));
    }
}