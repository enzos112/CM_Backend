package com.medico.backend.repository;

import com.medico.backend.model.core.Persona;
import com.medico.backend.model.core.Usuario;
import java.util.Optional;

public interface PersonaRepository extends IGenericRepository<Persona, Integer> {

    // Buscar persona por su usuario (Para el Login/Perfil)
    Optional<Persona> findByUsuario(Usuario usuario);

    // Validar si el DNI ya existe
    boolean existsByNumeroDocumento(String numeroDocumento);

    Optional<Persona> findByNumeroDocumento(String numeroDocumento);
    Optional<Persona> findByUsuarioEmail(String email);

}