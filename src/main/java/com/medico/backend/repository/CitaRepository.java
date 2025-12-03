package com.medico.backend.repository;

import com.medico.backend.model.administrative.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Validar si el médico ya tiene una cita ACTIVA (no cancelada) en esa fecha y hora
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.fechaHora = :fechaHora " +
            "AND c.estado != 'CANCELADA'")
    boolean existsByMedicoAndFechaHora(@Param("medicoId") Long medicoId,
                                       @Param("fechaHora") LocalDateTime fechaHora);

    // Listar citas de un paciente específico
    List<Cita> findByUsuarioPacienteId(Long pacienteId);
}