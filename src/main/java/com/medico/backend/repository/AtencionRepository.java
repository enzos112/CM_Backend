package com.medico.backend.repository;

import com.medico.backend.model.clinical.Atencion;
import java.util.List;
import java.util.Optional;

public interface AtencionRepository extends IGenericRepository<Atencion, Integer> {

    // Para cargar la atención asociada a una cita específica
    Optional<Atencion> findByCitaIdCita(Integer idCita);

    // Para ver el historial de atenciones de una Historia Clínica
    List<Atencion> findByHistoriaClinicaIdHistoriaOrderByFechaAtencionDesc(Integer idHistoria);
}