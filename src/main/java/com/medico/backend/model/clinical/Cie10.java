package com.medico.backend.model.clinical;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cie10")
public class Cie10 {
    @Id
    @Column(length = 10)
    private String codigo;

    private String descripcion;
}