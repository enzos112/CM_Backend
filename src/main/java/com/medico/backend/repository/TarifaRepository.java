package com.medico.backend.repository;

import com.medico.backend.model.infrastructure.Tarifa;
import com.medico.backend.model.infrastructure.Especialidad;
import com.medico.backend.model.infrastructure.ModalidadCita;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TarifaRepository extends IGenericRepository<Tarifa, Integer> {

    // Método para buscar la tarifa activa por Especialidad y Modalidad
    @Query("SELECT t FROM Tarifa t WHERE t.especialidad = :especialidad AND t.modalidad = :modalidad AND t.activo = true")
    Optional<Tarifa> findActiveByEspecialidadAndModalidad(
            @Param("especialidad") Especialidad especialidad,
            @Param("modalidad") ModalidadCita modalidad
    );
}