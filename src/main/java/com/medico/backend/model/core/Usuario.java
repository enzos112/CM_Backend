package com.medico.backend.model.core;

import com.medico.backend.model.security.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "El formato del correo es inválido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    // Mínimo 8 caracteres, al menos 1 mayúscula y 1 número
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número")
    private String password;

    // --- Datos Personales ---

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El tipo de documento debe ser DNI, CE o PASAPORTE")
    private String tipoDocumento; // DNI, PASAPORTE

    @NotBlank
    @Size(min = 8, max = 12, message = "El documento debe tener entre 8 y 12 caracteres")
    @Column(unique = true, nullable = false)
    private String numeroDocumento;

    @NotBlank
    // Validación: Solo letras y espacios, y NO permite 4 consonantes seguidas (evita nombres falsos como 'Hjlmp')
    @Pattern(regexp = "^(?!.*[b-df-hj-np-tv-zB-DF-HJ-NP-TV-Z]{4})[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
            message = "El nombre contiene caracteres inválidos o demasiadas consonantes seguidas")
    private String nombres;

    @NotBlank
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido paterno solo puede contener letras")
    private String apellidoPaterno;

    @NotBlank
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido materno solo puede contener letras")
    private String apellidoMaterno;

    @NotBlank
    // Empieza con 9 y tiene 9 dígitos en total
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe ser un celular válido de Perú (9 dígitos)")
    private String telefono;

    // --- Ubicación ---
    private String direccion;

    // Guardaremos el ID del distrito seleccionado en el frontend
    private Integer distritoId;

    // --- Seguridad de Cuenta ---
    @Enumerated(EnumType.STRING)
    private Rol rol; // PACIENTE, MEDICO, ADMIN

    private Integer intentosLogin = 0;
    private Boolean cuentaBloqueada = false;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.rol == null) this.rol = Rol.PACIENTE;

    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte tu ROL en un permiso que Spring entienda
        if (rol == null) return List.of(new SimpleGrantedAuthority("ROLE_PACIENTE"));
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return email; // Usamos el email para el login
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !Boolean.TRUE.equals(cuentaBloqueada); }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}