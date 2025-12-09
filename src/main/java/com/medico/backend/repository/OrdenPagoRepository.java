package com.medico.backend.repository;

import com.medico.backend.model.administrative.OrdenPago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OrdenPagoRepository extends IGenericRepository<OrdenPago, Integer> {


    // 3 comparar el día.
    @Query("SELECT SUM(o.montoTotal) FROM OrdenPago o WHERE CAST(o.fechaEmision AS LocalDate) = :fecha")
    Double sumByFecha(@Param("fecha") LocalDate fecha);

}