package com.medico.backend.model.clinical;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medico.backend.util.GeneradorCodigo;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReceta;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo; // RC-XXX-XXXXX

    @ManyToOne
    @JoinColumn(name = "id_atencion")
    @JsonIgnore // <--- AGREGAR ESTO
    private Atencion atencion;

    private LocalDateTime fechaEmision;
    private String urlPdfFirmado;

    @Column(columnDefinition = "TEXT")
    private String indicacionesGenerales;

    private Integer vigenciaDias;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<DetalleReceta> detalles;

    @PrePersist
    protected void onCreate() {
        if (this.codigo == null) this.codigo = GeneradorCodigo.generarCodigo("RC");
    }
}