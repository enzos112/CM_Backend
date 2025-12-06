package com.medico.backend.model.administrative;

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
    private OrdenPago ordenPago;

    @Column(length = 20)
    private String tipoItem;

    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;

    @OneToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;
}