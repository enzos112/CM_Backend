package com.medico.backend.model.privat;

import com.medico.backend.model.core.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "logs_actividad")
public class LogActividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLog;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(length = 50)
    private String accion;
    @Column(length = 50)
    private String entidadAfectada;
    private Integer idEntidad;

    @Column(columnDefinition = "TEXT")
    private String valoresAnteriores;
    @Column(columnDefinition = "TEXT")
    private String valoresNuevos;

    private String ipAddress;
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() { this.fecha = LocalDateTime.now(); }
}