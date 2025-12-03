package com.medico.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CitaResponse {
    private Long id;
    private LocalDateTime fechaHora;
    private String modalidad;
    private String estado;

    // Datos procesados (Más fáciles de leer para el Frontend)
    private String nombreMedico;
    private String especialidad;
    private String nombrePaciente;
    private String dniPaciente;

    // Links (útil para cuando integres S3)
    private String linkReunion;
}