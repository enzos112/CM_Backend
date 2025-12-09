package com.medico.backend.repository;

import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.infrastructure.Medico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MedicoRepository extends IGenericRepository<Medico, Integer> {
    Optional<Medico> findByCmp(String cmp);
    Optional<Medico> findByPersonaUsuario(Usuario usuario);

    // Buscar el perfil médico dado un usuario de sistema
    // Navegamos: Medico -> Persona -> Usuario
    @Query("SELECT m FROM Medico m WHERE m.persona.usuario = :usuario")
    Optional<Medico> findByUsuario(@Param("usuario") Usuario usuario);
    // Buscar al médico navegando por Persona -> Usuario -> Email
    @Query("SELECT m FROM Medico m WHERE m.persona.usuario.email = :email")
    Optional<Medico> findByUsuarioEmail(@Param("email") String email);

}