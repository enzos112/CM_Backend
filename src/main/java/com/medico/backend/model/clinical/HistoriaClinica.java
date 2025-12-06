package com.medico.backend.model.clinical;

import com.medico.backend.model.core.Persona;
import com.medico.backend.util.GeneradorCodigo;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historias_clinicas")
public class HistoriaClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistoria;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo; // HC-XXX-XXXXX

    @OneToOne
    @JoinColumn(name = "id_persona_paciente")
    private Persona paciente;

    private LocalDateTime fechaApertura;

    @PrePersist
    protected void onCreate() {
        this.fechaApertura = LocalDateTime.now();
        if (this.codigo == null) this.codigo = GeneradorCodigo.generarCodigo("HC");
    }
}