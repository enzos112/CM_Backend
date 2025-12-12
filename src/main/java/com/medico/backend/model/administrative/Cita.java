package com.medico.backend.model.administrative;

import com.medico.backend.model.core.Persona;
import com.medico.backend.model.infrastructure.Especialidad;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.model.infrastructure.ModalidadCita;
import com.medico.backend.model.infrastructure.Tarifa;
// import com.medico.backend.util.GeneradorCodigo; // Descomenta si tienes la clase
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCita;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "id_persona_paciente", nullable = false)
    private Persona paciente;

    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "id_especialidad")
    private Especialidad especialidad;

    @ManyToOne
    @JoinColumn(name = "id_tarifa")
    private Tarifa tarifa; // Puede ser null inicialmente si no hay pago previo

    @ManyToOne
    @JoinColumn(name = "id_modalidad", nullable = false)
    private ModalidadCita modalidad;

    // --- RELACIÓN INVERSA (Sin JoinColumn) ---
    // Esto permite acceder al pago desde la cita: cita.getDetalleOrden().getOrdenPago()
    @OneToOne(mappedBy = "cita", fetch = FetchType.LAZY)
    @JsonIgnore // Evita error de recursión infinita si devuelves la Cita en un JSON
    private DetalleOrden detalleOrden;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @Column(length = 20)
    private String estado; // PENDIENTE, PAGADO, FINALIZADO

    private String motivoConsultaPaciente;
    private String linkReunion;
    private String origenReserva;

    // --- NUEVOS CAMPOS PARA DOMICILIO ---
    @Column(length = 50)
    private String distrito; // Ej: "Victor Larco", "Huanchaco"

    @Column(length = 150)
    private String direccionExacta; // Ej: "Av. Larco 123, Dpto 402"

    @Column(length = 100)
    private String referencia; // Ej: "Frente al parque de las aguas"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.codigo == null) this.codigo = "CT-" + System.currentTimeMillis();
    }

    // RELACIÓN INVERSA CON SOLICITUD
    @OneToOne(mappedBy = "cita", fetch = FetchType.LAZY)
    private SolicitudCancelacion solicitudCancelacion;

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}