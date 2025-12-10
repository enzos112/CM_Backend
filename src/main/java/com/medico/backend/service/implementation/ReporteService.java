package com.medico.backend.service.implementation;

import com.medico.backend.repository.CitaRepository;
import com.medico.backend.repository.OrdenPagoRepository;
import com.medico.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final OrdenPagoRepository ordenPagoRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;

    // Lógica 1: Reporte de Dinero
    public Map<String, Object> obtenerIngresosMensuales(int anio, int mes) {
        Double total = ordenPagoRepository.sumarIngresosMensuales(anio, mes);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("anio", anio);
        respuesta.put("mes", mes);
        respuesta.put("ingresosTotal", total != null ? total : 0.0);

        return respuesta;
    }

    // Lógica 2: Reporte de Estados (Pastel)
    public List<Map<String, Object>> obtenerEstadisticasCitas() {
        List<Object[]> resultados = citaRepository.contarCitasPorEstado();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> map = new HashMap<>();
            map.put("estado", fila[0]);
            map.put("cantidad", fila[1]);
            lista.add(map);
        }
        return lista;
    }

    // Lógica 3: Top 5 Médicos
    public List<Map<String, Object>> obtenerTop5Medicos() {
        // Pedimos solo los 5 primeros
        List<Object[]> resultados = citaRepository.encontrarTopMedicos(PageRequest.of(0, 5));

        List<Map<String, Object>> lista = new ArrayList<>();
        for (Object[] fila : resultados) {
            Map<String, Object> map = new HashMap<>();
            String nombreCompleto = fila[0] + " " + fila[1]; // Nombre + Apellido
            map.put("medico", nombreCompleto);
            map.put("totalCitas", fila[2]);
            lista.add(map);
        }
        return lista;
    }

    // 4. Lógica Top Especialidades
    public List<Map<String, Object>> obtenerTopEspecialidades() {
        List<Object[]> resultados = citaRepository.contarCitasPorEspecialidad();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> map = new HashMap<>();
            map.put("especialidad", fila[0]);
            map.put("cantidad", fila[1]);     // Cantidad Citas
            lista.add(map);
        }
        return lista;
    }

    // 5. Lógica Crecimiento Pacientes
    public List<Map<String, Object>> obtenerCrecimientoPacientes() {
        List<Object[]> resultados = usuarioRepository.crecimientoUsuariosAnual();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> map = new HashMap<>();
            map.put("mes", fila[0]);
            map.put("cantidad", fila[1]); // Usuarios nuevos
            lista.add(map);
        }
        return lista;
    }

    // 6. Lógica KPI Tasa de Cancelación
    public Map<String, Object> obtenerTasaCancelacion() {
        long totalCitas = citaRepository.count();
        long canceladas = citaRepository.countByEstado("CANCELADO");

        double tasa = 0.0;
        if (totalCitas > 0) {
            tasa = ((double) canceladas / totalCitas) * 100;
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("totalCitas", totalCitas);
        respuesta.put("citasCanceladas", canceladas);
        respuesta.put("tasaCancelacion", Math.round(tasa * 100.0) / 100.0);

        return respuesta;
    }
}