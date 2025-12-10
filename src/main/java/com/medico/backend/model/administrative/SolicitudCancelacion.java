package com.medico.backend.model.administrative;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medico.backend.model.core.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitudes_cancelacion")
public class SolicitudCancelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;

    // RELACIÓN 1:1 CON CITA (Dueña de la relación)
    @OneToOne
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    @JsonIgnore // <--- AGREGAR ESTO
    private Cita cita;

    // RELACIÓN N:1 CON USUARIO (Quién solicita)
    @ManyToOne
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    private Usuario usuarioSolicitante;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(length = 500)
    private String evidenciaUrl;

    private LocalDateTime fechaSolicitud;

    @Column(length = 20)
    private String estadoSolicitud; // PENDIENTE, APROBADA, RECHAZADA

    @Column(length = 500)
    private String respuestaAdmin;

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
        this.estadoSolicitud = "PENDIENTE";
    }
}