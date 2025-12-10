package com.medico.backend.repository;

import com.medico.backend.model.clinical.HistoriaClinica;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface HistoriaClinicaRepository extends IGenericRepository<HistoriaClinica, Integer> {

    // Buscar historia por el ID del Paciente (Persona)
    @Query("SELECT h FROM HistoriaClinica h WHERE h.paciente.idPersona = :idPersona")
    Optional<HistoriaClinica> findByPacienteId(@Param("idPersona") Integer idPersona);
}