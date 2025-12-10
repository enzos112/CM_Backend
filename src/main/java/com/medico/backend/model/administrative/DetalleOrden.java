package com.medico.backend.model.administrative;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalles_orden")
public class DetalleOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_orden", nullable = false)
    @JsonIgnore // <--- AGREGAR ESTO
    private OrdenPago ordenPago;

    @Column(length = 20)
    private String tipoItem;

    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;

    // RELACIÓN DUEÑA CON CITA:
    // Esto creará la columna física 'id_cita' en la tabla 'detalles_orden'.
    // Usamos unique=true porque una cita solo puede estar en un detalle.
    @OneToOne
    @JoinColumn(name = "id_cita", referencedColumnName = "idCita")
    private Cita cita;
}