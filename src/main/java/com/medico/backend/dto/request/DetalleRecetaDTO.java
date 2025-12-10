package com.medico.backend.dto.request;

import lombok.Data;

@Data
public class DetalleRecetaDTO {
    private String medicamento;
    private String dosis;
    private String frecuencia;
    private String duracion;
}