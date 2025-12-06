package com.medico.backend.repository;

import com.medico.backend.model.infrastructure.ModalidadCita;
import java.util.Optional;

public interface ModalidadCitaRepository extends IGenericRepository<ModalidadCita, Integer> {
    Optional<ModalidadCita> findByNombre(String nombre);
}