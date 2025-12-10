package com.medico.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AtencionRequest {

    @NotNull(message = "El ID de la cita es obligatorio")
    private Integer idCita;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    private String motivoConsulta;

    private String exploracionFisica;

    // Signos Vitales (Opcionales o con @NotNull si son obligatorios)
    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Integer saturacionOxigeno;
    private BigDecimal peso;
    private BigDecimal talla;
    private String presionArterial;

    // Diagnóstico
    @NotBlank(message = "El diagnóstico presuntivo es obligatorio")
    private String diagnosticoPresuntivo;

    private String diagnosticoDefinitivo;
    private String codigoCie10;

    // Tratamiento
    @NotBlank(message = "El plan de tratamiento es obligatorio")
    private String planTratamiento;

    private String pronostico;

    // Validamos también la lista de medicamentos si viene
    @Valid
    private List<DetalleRecetaDTO> medicamentos;
}