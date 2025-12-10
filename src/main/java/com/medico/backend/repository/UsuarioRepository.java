package com.medico.backend.repository;

import com.medico.backend.model.core.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends IGenericRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    // NUEVO: Busca el usuario a través de la tabla Personas
    // "Selecciona el usuario 'u' desde la entidad Persona 'p' donde su documento sea :dni"
    @Query("SELECT p.usuario FROM Persona p WHERE p.numeroDocumento = :dni")
    Optional<Usuario> findByDni(@Param("dni") String dni);

    // REPORTE 4: Crecimiento de Pacientes (Nuevos Usuarios)
    // JOIN con usuarioRoles para filtrar por 'PACIENTE'
    @Query("SELECT MONTH(u.fechaRegistro), COUNT(u) " +
            "FROM Usuario u JOIN u.usuarioRoles ur " +
            "WHERE ur.rol.nombreRol = 'PACIENTE' " +
            "AND YEAR(u.fechaRegistro) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(u.fechaRegistro)")
    List<Object[]> crecimientoUsuariosAnual();
}