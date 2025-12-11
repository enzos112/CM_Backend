package com.medico.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailRequest {
    @NotBlank(message = "El nuevo email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String newEmail;
}