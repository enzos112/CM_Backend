package com.medico.backend.model.clinical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "signos_vitales")
public class SignoVital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSigno;

    @ManyToOne
    @JoinColumn(name = "id_atencion")
    @JsonIgnore // <--- AGREGAR ESTO
    private Atencion atencion;

    // Opcionales (pueden ser nulos en citas virtuales)
    @Column(length = 10)
    private String presionArterial;

    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Integer saturacionOxigeno;
    private BigDecimal peso;
    private BigDecimal talla;
    private BigDecimal imc;
}