package com.medico.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CitaRequest {

    @NotNull(message = "Debes seleccionar un médico (ID obligatorio)")
    private Integer medicoId;

    @NotNull(message = "La fecha y hora son obligatorias")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotNull(message = "La modalidad es obligatoria")
    private Integer modalidadId;

    private boolean esParaTercero;
    private String pacienteNombre;
    private String pacienteDni;
    private String pacienteTelefono;

    private String motivoConsulta; // Opcional al agendar
    private BigDecimal peso;
    private BigDecimal altura;
    private String alergias;
    private String antecedentes;
}