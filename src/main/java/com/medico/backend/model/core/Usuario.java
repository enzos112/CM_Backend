package com.medico.backend.model.core;

import com.medico.backend.model.privat.UsuarioRol;
import com.medico.backend.util.GeneradorCodigo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Código público (Ej: US-482-X9J2M)
    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "estado_cuenta", length = 20)
    private String estadoCuenta;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    // RELACIÓN: Un Usuario tiene muchos registros en UsuarioRol
    // Fetch EAGER es obligatorio para Login (cargar roles al iniciar sesión)
    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UsuarioRol> usuarioRoles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.estadoCuenta == null) this.estadoCuenta = "ACTIVO";

        if (this.codigo == null) {
            this.codigo = GeneradorCodigo.generarCodigo("US");
            this.codigo = "US-" + System.currentTimeMillis();
        }
    }

    // --- MÉTODOS OBLIGATORIOS DE SPRING SECURITY ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (usuarioRoles == null) return List.of();

        // Navegamos: Usuario -> UsuarioRol -> Rol -> nombreRol
        return usuarioRoles.stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRol().getNombreRol()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !"BLOQUEADO".equals(estadoCuenta); }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return !"INACTIVO".equals(estadoCuenta); }
}