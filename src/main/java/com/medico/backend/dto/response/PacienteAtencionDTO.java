package com.medico.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PacienteAtencionDTO {
    private Long idAtencion;
    private LocalDateTime fecha;
    private String medico;
    private String especialidad;

    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;

    private List<MedicamentoDTO> receta;

    @Data
    @Builder
    public static class MedicamentoDTO {
        private String medicina;
        private String dosis;
        private String indicaciones;
    }
}