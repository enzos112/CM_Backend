package com.medico.backend.repository;

import com.medico.backend.model.privat.Rol;
import java.util.Optional;

public interface RolRepository extends IGenericRepository<Rol, Integer> {
    Optional<Rol> findByNombreRol(String nombreRol);
}