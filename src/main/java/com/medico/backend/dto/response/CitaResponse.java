package com.medico.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CitaResponse {
    private Long id; // Usamos Long para flexibilidad en el frontend, aunque la BD sea Integer
    private LocalDateTime fechaHora;
    private String modalidad;
    private String estado;
    private String nombreMedico;
    private String especialidad;
    private String nombrePaciente;
    private String dniPaciente;
    private String linkReunion;
}