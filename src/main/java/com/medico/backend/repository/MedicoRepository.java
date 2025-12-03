package com.medico.backend.repository;

import com.medico.backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    // Método extra útil: Buscar el perfil médico usando el ID del usuario
    // (Servirá para cuando el médico inicie sesión y quiera ver sus citas)
    Optional<Medico> findByUsuarioId(Long usuarioId);

    // Buscar por CMP (Colegiatura)
    Optional<Medico> findByCmp(String cmp);
}