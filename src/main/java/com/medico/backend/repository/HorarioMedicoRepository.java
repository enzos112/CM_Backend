package com.medico.backend.repository;

import com.medico.backend.model.infrastructure.HorarioMedico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;
import java.util.List;

public interface HorarioMedicoRepository extends IGenericRepository<HorarioMedico, Integer> {

    // Listar horarios de un médico específico
    List<HorarioMedico> findByMedicoIdMedico(Integer idMedico);

    // Validar cruce de horarios:
    // Un horario se cruza si: (NuevoInicio < ViejoFin) Y (NuevoFin > ViejoInicio)
    // Además debe ser el mismo día y el mismo médico.
    @Query("SELECT COUNT(h) > 0 FROM HorarioMedico h " +
            "WHERE h.medico.idMedico = :medicoId " +
            "AND h.diaSemana = :dia " +
            "AND h.horaInicio < :fin " +
            "AND h.horaFin > :inicio " +
            "AND h.activo = true")
    boolean existeCruce(
            @Param("medicoId") Integer medicoId,
            @Param("dia") Integer dia,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin
    );
}