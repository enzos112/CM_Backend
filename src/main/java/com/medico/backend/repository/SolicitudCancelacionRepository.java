package com.medico.backend.repository;

import com.medico.backend.model.administrative.SolicitudCancelacion;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SolicitudCancelacionRepository extends IGenericRepository<SolicitudCancelacion, Integer> {

    // Buscar pendientes para el dashboard del admin
    List<SolicitudCancelacion> findByEstadoSolicitud(String estadoSolicitud);
}