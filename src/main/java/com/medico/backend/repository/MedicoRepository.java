package com.medico.backend.repository;

import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.infrastructure.Medico;
import java.util.Optional;

public interface MedicoRepository extends IGenericRepository<Medico, Integer> {
    Optional<Medico> findByCmp(String cmp);
    Optional<Medico> findByPersonaUsuario(Usuario usuario);

}