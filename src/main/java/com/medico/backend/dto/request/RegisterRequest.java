package com.medico.backend.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {

    // CAMPOS DE USUARIO
    private String email;
    private String password;

    // CAMPOS DE PERSONA
    private String tipoDocumento;
    private String numeroDocumento;

    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;

    private LocalDate fechaNacimiento;
    private String genero;
    private String telefonoMovil;

    // CAMPOS DE DIRECCIÓN
    private String region;
    private String provincia;
    private String distrito;
    private String direccionCalle;

    // CAMPOS DE EMERGENCIA
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
}