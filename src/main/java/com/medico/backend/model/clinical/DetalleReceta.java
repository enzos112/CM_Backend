package com.medico.backend.model.clinical;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_receta")
public class DetalleReceta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleReceta;

    @ManyToOne
    @JoinColumn(name = "id_receta")
    private Receta receta;

    @Column(length = 100)
    private String medicamentoNombre;

    @Column(length = 50)
    private String dosis;

    @Column(length = 50)
    private String frecuencia;

    @Column(length = 50)
    private String duracion;
}