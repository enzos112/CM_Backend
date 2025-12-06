package com.medico.backend.model.core;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "relaciones_familiares")
public class RelacionFamiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRelacion;

    @ManyToOne
    @JoinColumn(name = "id_persona_tutor")
    private Persona tutor;

    @ManyToOne
    @JoinColumn(name = "id_persona_dependiente")
    private Persona dependiente;

    @Column(length = 20)
    private String tipoRelacion; // PADRE, MADRE, HIJO

    private boolean esRepresentanteLegal;
    private String documentoVerificacionUrl;
}