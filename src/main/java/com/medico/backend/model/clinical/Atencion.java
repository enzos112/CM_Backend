package com.medico.backend.model.clinical;

import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.util.GeneradorCodigo;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "atenciones")
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAtencion;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo; // AT-XXX-XXXXX

    @OneToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "id_historia")
    private HistoriaClinica historiaClinica;

    @ManyToOne
    @JoinColumn(name = "id_medico")
    private Medico medico;

    private LocalDateTime fechaAtencion;

    // SOAP
    @Column(columnDefinition = "TEXT")
    private String motivoConsulta;
    @Column(columnDefinition = "TEXT")
    private String exploracionFisica;

    private String diagnosticoPresuntivo;
    private String diagnosticoDefinitivo;

    @ManyToOne
    @JoinColumn(name = "cie10_codigo")
    private Cie10 cie10;

    @Column(columnDefinition = "TEXT")
    private String planTratamiento;
    private String pronostico;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL)
    private List<SignoVital> signosVitales;

    @PrePersist
    protected void onCreate() {
        if (this.codigo == null) this.codigo = GeneradorCodigo.generarCodigo("AT");
    }
}