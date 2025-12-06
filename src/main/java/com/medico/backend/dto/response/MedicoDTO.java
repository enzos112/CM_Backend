package com.medico.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicoDTO {
    private Integer idMedico;
    private String nombreCompleto;
    private String cmp;
    private Integer idEspecialidad;
}