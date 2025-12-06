package com.medico.backend.repository;

import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.core.Persona;
import com.medico.backend.model.infrastructure.Medico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends IGenericRepository<Cita, Integer> {

    // Método 1: Validación rápida para evitar doble agendamiento al guardar
    boolean existsByMedicoAndFechaHoraInicio(Medico medico, LocalDateTime fechaHoraInicio);

    // Método 2: Para pintar la grilla en el Frontend (Trae las horas ocupadas)
    // Filtramos para no traer citas que hayan sido CANCELADAS
    @Query("SELECT c.fechaHoraInicio FROM Cita c " +
            "WHERE c.medico.idMedico = :medicoId " +
            "AND c.fechaHoraInicio BETWEEN :inicio AND :fin " +
            "AND c.estado != 'CANCELADO'")
    List<LocalDateTime> findHorasOcupadas(
            @Param("medicoId") Integer medicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Listar historial de un paciente
    List<Cita> findByPaciente(Persona paciente);
}