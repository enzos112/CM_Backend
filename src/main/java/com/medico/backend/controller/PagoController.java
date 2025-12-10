package com.medico.backend.controller;

import com.medico.backend.model.administrative.ComprobanteSunat;
import com.medico.backend.model.administrative.OrdenPago;
import com.medico.backend.repository.OrdenPagoRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/intranet/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final OrdenPagoRepository ordenPagoRepository;

    // Endpoint para que el ADMIN/CAJERO confirme que recibió el dinero
    @PostMapping("/{idOrden}/confirmar")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo Admin por ahora
    public ResponseEntity<OrdenPago> confirmarPago(@PathVariable Integer idOrden) {

        OrdenPago orden = ordenPagoRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden de pago no encontrada"));

        if ("PAGADO".equals(orden.getEstado())) {
            throw new RuntimeException("Esta orden ya fue pagada anteriormente.");
        }

        // 1. Cambiar estado
        orden.setEstado("PAGADO");
        orden.setCodigoOperacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Simula código de voucher

        // 2. Generar Comprobante (Simulación SUNAT)
        // Como la relación es 1 a 1 y ya arreglamos la BD, podemos crear el objeto aquí
        ComprobanteSunat comprobante = new ComprobanteSunat();
        comprobante.setTipo("BOLETA");
        comprobante.setSerie("B001");
        comprobante.setNumero("000" + orden.getIdOrden());
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setMontoTotal(orden.getMontoTotal());
        comprobante.setClienteNombre(orden.getUsuarioPagador().getEmail()); // O el nombre de la persona
        comprobante.setEstadoSunat("ACEPTADO");

        // Vincular
        orden.setComprobante(comprobante);

        // Guardar (al tener CascadeType.ALL, guarda orden y comprobante)
        return ResponseEntity.ok(ordenPagoRepository.save(orden));
    }
}