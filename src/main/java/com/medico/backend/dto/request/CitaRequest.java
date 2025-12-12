package com.medico.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CitaRequest {

    @NotNull(message = "Debes seleccionar un médico")
    private Integer medicoId;

    @NotNull(message = "La fecha y hora son obligatorias")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotNull(message = "La modalidad es obligatoria")
    private Integer modalidadId;

    private String distrito;
    private String direccionExacta;
    private String referencia;

    // --- DATOS PARA TERCEROS (Actualizado según Prototipo) ---
    private boolean esParaTercero;

    private String pacienteNombre;    // "Nombres" en el formulario
    private String pacienteApellido;  // <--- NUEVO: "Apellidos"
    private String pacienteDni;       // "DNI"
    private String pacienteEmail;     // <--- NUEVO: "Correo electrónico"
    private String pacienteTelefono;  // "Número de teléfono"

    // Datos médicos opcionales
    private String motivoConsulta;
    private BigDecimal peso;
    private BigDecimal altura;
    private String alergias;
    private String antecedentes;
}