package com.medico.backend.model.administrative;

import com.medico.backend.model.ModalidadCita;
import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.infrastructure.Medico;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_paciente_id")
    private Usuario usuarioPaciente; // Quien hace la reserva (Dueño de la cuenta)

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @NotNull
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    private ModalidadCita modalidad; // PRESENCIAL, VIRTUAL, DOMICILIO

    private String estado; // PROGRAMADA, FINALIZADA, CANCELADA
    private String linkReunion; // Para Zoom/Meet

    // --- Datos para "Cita para otra persona" ---
    // Si es false, se usan los datos de usuarioPaciente.
    // Si es true, se usan los datos de abajo.
    private boolean esParaTercero;

    private String pacienteNombre;
    private String pacienteDni;
    private String pacienteTelefono;

    // --- Información Médica (Triage) ---
    private BigDecimal peso;
    private BigDecimal altura;
    private String motivoConsulta;

    @Column(length = 500)
    private String alergias;

    @Column(length = 1000)
    private String antecedentes;

    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) this.estado = "PROGRAMADA";
    }
}