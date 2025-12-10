package com.medico.backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AtencionRequest {

    private Integer idCita; // ID de la cita que se está atendiendo

    // --- SOAP: Subjetivo (Lo que dice el paciente) ---
    private String motivoConsulta;

    // --- SOAP: Objetivo (Lo que mide el médico) ---
    private String exploracionFisica;

    // Signos Vitales
    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Integer saturacionOxigeno;
    private BigDecimal peso;
    private BigDecimal talla;
    private String presionArterial;

    // --- SOAP: Análisis (Diagnóstico) ---
    private String diagnosticoPresuntivo;
    private String diagnosticoDefinitivo;
    private String codigoCie10; // El código seleccionado (ej: "J00")

    // --- SOAP: Plan (Tratamiento) ---
    private String planTratamiento;
    private String pronostico;

    private List<DetalleRecetaDTO> medicamentos;
}