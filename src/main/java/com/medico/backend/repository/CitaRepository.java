package com.medico.backend.repository;

import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.core.Persona;
import com.medico.backend.model.infrastructure.Medico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends IGenericRepository<Cita, Integer> {

    // Método 1: Validación rápida
    boolean existsByMedicoAndFechaHoraInicio(Medico medico, LocalDateTime fechaHoraInicio);

    // Método 2: Pintar la grilla (Horas ocupadas)
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


    // c.medico -> .persona -> .usuario -> .email
    @Query("SELECT c FROM Cita c WHERE c.medico.persona.usuario.email = :email AND CAST(c.fechaHoraInicio AS LocalDate) = :fecha")
    List<Cita> findByMedicoUsuarioEmailAndFecha(@Param("email") String email, @Param("fecha") LocalDate fecha);

    // Búsqueda para el historial del paciente
    @Query("SELECT c FROM Cita c WHERE c.paciente.usuario.email = :email")
    List<Cita> findByPacienteUsuarioEmail(@Param("email") String email);
}