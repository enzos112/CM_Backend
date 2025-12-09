package com.medico.backend.repository;

import com.medico.backend.model.administrative.OrdenPago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OrdenPagoRepository extends IGenericRepository<OrdenPago, Integer> {


    //  comparar el día.
    @Query("SELECT SUM(o.montoTotal) FROM OrdenPago o WHERE CAST(o.fechaEmision AS LocalDate) = :fecha")
    Double sumByFecha(@Param("fecha") LocalDate fecha);
    // REPORTE1 : Ingresos Totales del Mes
    @Query("SELECT SUM(o.montoTotal) FROM OrdenPago o " +
            "WHERE YEAR(o.fechaEmision) = :anio AND MONTH(o.fechaEmision) = :mes")
    Double sumarIngresosMensuales(@Param("anio") int anio, @Param("mes") int mes);

}