package com.medico.backend.model.infrastructure;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "horarios_medicos")
public class HorarioMedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHorario;

    @ManyToOne
    @JoinColumn(name = "id_medico")
    private Medico medico;

    @ManyToOne // Relación con la tabla maestra
    @JoinColumn(name = "id_modalidad")
    private ModalidadCita modalidad;

    private Integer diaSemana; // 1=Lunes
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private boolean activo;
}