package com.medico.backend.model.administrative;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comprobantes_sunat")
public class ComprobanteSunat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComprobante;

    @Column(length = 20)
    private String tipo; // BOLETA, FACTURA
    @Column(length = 10)
    private String serie;
    @Column(length = 20)
    private String numero;

    private LocalDateTime fechaEmision;
    private String clienteNombre;
    private String clienteDocumento;
    private String clienteDireccion;
    private BigDecimal montoSubtotal;
    private BigDecimal montoIgv;
    private BigDecimal montoTotal;

    @Column(length = 20)
    private String estadoSunat;

    private String xmlUrl;
    private String pdfUrl;
    private String hashCpe;
}