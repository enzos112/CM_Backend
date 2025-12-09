package com.medico.backend.model.administrative;

import com.medico.backend.model.core.Usuario;
import com.medico.backend.util.GeneradorCodigo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordenes_pago")
public class OrdenPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrden;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    // Relación limpia con Comprobante (La que arreglamos antes)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comprobante_ref_id", referencedColumnName = "idComprobante")
    private ComprobanteSunat comprobante;

    @ManyToOne
    @JoinColumn(name = "id_usuario_pagador")
    private Usuario usuarioPagador;

    private LocalDateTime fechaEmision;
    private BigDecimal montoTotal;

    @Column(length = 50)
    private String metodoPago;

    @Column(length = 50)
    private String codigoOperacion;

    @Column(length = 20)
    private String estado;

    @OneToMany(mappedBy = "ordenPago", cascade = CascadeType.ALL)
    private List<DetalleOrden> detalles;

    @PrePersist
    protected void onCreate() {
        if (this.codigo == null) this.codigo = GeneradorCodigo.generarCodigo("OP");
        if (this.fechaEmision == null) this.fechaEmision = LocalDateTime.now();
    }
}