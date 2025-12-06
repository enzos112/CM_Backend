package com.medico.backend.model.infrastructure;

import com.medico.backend.model.core.Persona;
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
@Table(name = "medicos")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMedico;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @OneToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    @Column(unique = true, length = 20)
    private String cmp;

    @Column(unique = true, length = 20)
    private String rne;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    private String firmaDigitalUrl;
    private String estadoOperativo; // ACTIVO, DE_VACACIONES

    @PrePersist
    protected void onCreate() {
        // Generación temporal simple para que no falle si no tienes la clase Utils
        if (this.codigo == null) this.codigo = "MED-" + System.currentTimeMillis();
    }
}