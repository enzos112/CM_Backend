package com.medico.backend.model.infrastructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "modalidades_cita")
public class ModalidadCita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModalidad;

    @Column(unique = true, length = 50)
    private String nombre;

    private String descripcion;
    private boolean activo;
}