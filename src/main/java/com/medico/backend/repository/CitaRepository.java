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

    // --- MÉTODOS COMPARTIDOS / TUYOS ---
    boolean existsByMedicoAndFechaHoraInicio(Medico medico, LocalDateTime fechaHoraInicio);

    @Query("SELECT c.fechaHoraInicio FROM Cita c " +
            "WHERE c.medico.idMedico = :medicoId " +
            "AND c.fechaHoraInicio BETWEEN :inicio AND :fin " +
            "AND c.estado != 'CANCELADO'")
    List<LocalDateTime> findHorasOcupadas(
            @Param("medicoId") Integer medicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    List<Cita> findByPaciente(Persona paciente);

    // --- MÉTODOS DE TU COMPAÑERA (DASHBOARD & PERFIL) ---
    // Usados en MedicoDashboardController y PerfilPacienteController

    @Query("SELECT c FROM Cita c WHERE c.medico.persona.usuario.email = :email AND CAST(c.fechaHoraInicio AS LocalDate) = :fecha")
    List<Cita> findByMedicoUsuarioEmailAndFecha(@Param("email") String email, @Param("fecha") LocalDate fecha);

    @Query("SELECT c FROM Cita c WHERE c.paciente.usuario.email = :email")
    List<Cita> findByPacienteUsuarioEmail(@Param("email") String email);

    // --- TUS NUEVOS MÉTODOS (AGENDA MÉDICO) ---
    // Usado en CitaService para tu lógica de rangos
    List<Cita> findByMedicoAndFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(
            Medico medico,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}