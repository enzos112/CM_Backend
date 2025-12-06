package com.medico.backend.repository;

import com.medico.backend.model.administrative.OrdenPago;

public interface OrdenPagoRepository extends IGenericRepository<OrdenPago, Integer> {
    // Aquí podemos añadir findByUsuarioPagador, findByCodigo, etc.
}