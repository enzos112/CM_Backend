package com.medico.backend.model.administrative;

import com.medico.backend.model.core.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "solicitudes_reprogramacion")
public class SolicitudReprogramacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "id_usuario_solicitante")
    private Usuario usuarioSolicitante;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    private LocalDateTime nuevaFechaPropuesta;

    @Column(length = 20)
    private String estado; // PENDIENTE, APROBADA

    @Column(columnDefinition = "TEXT")
    private String respuestaAdmin;
}