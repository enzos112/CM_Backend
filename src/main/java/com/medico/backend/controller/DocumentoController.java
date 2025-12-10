package com.medico.backend.controller;

import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.administrative.OrdenPago;
import com.medico.backend.repository.CitaRepository;
import com.medico.backend.repository.OrdenPagoRepository;
import com.medico.backend.service.implementation.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final PdfService pdfService;
    private final CitaRepository citaRepository;
    private final OrdenPagoRepository ordenPagoRepository;

    // Endpoint A: Receta
    @GetMapping("/receta/{idCita}")
    public void descargarReceta(HttpServletResponse response, @PathVariable Integer idCita) throws IOException {
        response.setContentType("application/pdf");

        // Configuracion para que el navegador sepa que es un archivo descargable
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=receta_" + idCita + ".pdf";
        response.setHeader(headerKey, headerValue);

        // Buscar cita
        Cita cita = citaRepository.findById(idCita).orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Generar PDF
        pdfService.generarRecetaPdf(response, cita);
    }

    // Endpoint B: Boleta
    @GetMapping("/boleta/{idPago}")
    public void descargarBoleta(HttpServletResponse response, @PathVariable Integer idPago) throws IOException {
        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=boleta_" + idPago + ".pdf";
        response.setHeader(headerKey, headerValue);

        OrdenPago pago = ordenPagoRepository.findById(idPago).orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        pdfService.generarBoletaPdf(response, pago);
    }
}