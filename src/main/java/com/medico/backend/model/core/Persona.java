package com.medico.backend.model.core;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersona;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false, length = 20)
    private String tipoDocumento;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroDocumento;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(length = 50)
    private String apellidoMaterno;

    private LocalDate fechaNacimiento;

    @Column(length = 20)
    private String genero;

    @Column(length = 20)
    private String telefonoMovil;

    // --- CORRECCIÓN: CAMPOS DE DIRECCIÓN ESTRUCTURADA ---
    private String region;
    private String provincia;
    private String distrito;
    @Column(name = "direccion_calle") // Calle y número final
    private String direccionCalle;

    // --- CONTACTO DE EMERGENCIA ---
    @Column(name = "contacto_emergencia_nombre", length = 100)
    private String contactoEmergenciaNombre;

    @Column(name = "contacto_emergencia_telefono", length = 20)
    private String contactoEmergenciaTelefono;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if(this.tipoDocumento == null) this.tipoDocumento = "DNI";
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}