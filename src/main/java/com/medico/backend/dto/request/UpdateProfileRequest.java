package com.medico.backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Pattern(regexp = "^9[0-9]{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefonoMovil;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccionCalle;

    // Contacto de Emergencia
    private String contactoEmergenciaNombre;

    @Pattern(regexp = "^9[0-9]{8}$", message = "El teléfono de emergencia debe tener 9 dígitos")
    private String contactoEmergenciaTelefono;
}