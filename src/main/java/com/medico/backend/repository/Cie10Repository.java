package com.medico.backend.repository;

import com.medico.backend.model.clinical.Cie10;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// Nota: Cie10 tiene ID String (código), así que usamos JpaRepository<Cie10, String>
public interface Cie10Repository extends JpaRepository<Cie10, String> {

    // Buscador para el autocompletado del diagnóstico
    @Query("SELECT c FROM Cie10 c WHERE c.codigo LIKE %:term% OR c.descripcion LIKE %:term%")
    List<Cie10> buscarPorCodigoONombre(@Param("term") String term);
}