package com.medico.backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String password;
    private String telefono;
    private String dni; // numeroDocumento
}