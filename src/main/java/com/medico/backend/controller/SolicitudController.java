package com.medico.backend.controller;

import com.medico.backend.model.administrative.SolicitudCancelacion;
import com.medico.backend.service.implementation.CitaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final CitaService citaService;

    // PACIENTE: Crea la solicitud
    @PostMapping("/crear")
    public ResponseEntity<Void> crearSolicitud(@RequestBody CrearSolicitudRequest req) {
        citaService.solicitarCancelacion(req.citaId, req.motivo, req.evidenciaUrl);
        return ResponseEntity.ok().build();
    }

    // ADMIN/MÉDICO: Ver pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudCancelacion>> listarPendientes() {
        return ResponseEntity.ok(citaService.listarSolicitudesPendientes());
    }

    // ADMIN/MÉDICO: Resolver
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<Void> resolver(@PathVariable Integer id, @RequestBody ResolverRequest req) {
        citaService.evaluarSolicitud(id, req.aprobado, req.respuesta);
        return ResponseEntity.ok().build();
    }

    // DTOs Internos
    @Data static class CrearSolicitudRequest {
        Integer citaId; String motivo; String evidenciaUrl;
    }
    @Data static class ResolverRequest {
        Boolean aprobado; String respuesta;
    }
}