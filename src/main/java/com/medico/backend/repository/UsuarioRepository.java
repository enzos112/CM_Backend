package com.medico.backend.repository;

import com.medico.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // Métodos auxiliares para validar el Registro (evitar duplicados)
    boolean existsByEmail(String email);

    boolean existsByNumeroDocumento(String numeroDocumento);
}