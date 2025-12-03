package com.medico.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medico.backend.model.ModalidadCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CitaRequest {

    @NotNull(message = "Debes seleccionar un médico")
    private Long medicoId;

    @NotNull(message = "La fecha y hora son obligatorias")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm") // Formato: 2025-10-30 15:30
    private LocalDateTime fechaHora;

    @NotNull(message = "La modalidad es obligatoria")
    private ModalidadCita modalidad;

    // --- Datos para Terceros ---
    private boolean esParaTercero;
    private String pacienteNombre;
    private String pacienteDni;
    private String pacienteTelefono;

    // --- Triage Básico ---
    private String motivoConsulta;
    private BigDecimal peso;
    private BigDecimal altura;
    private String alergias;
    private String antecedentes;
}