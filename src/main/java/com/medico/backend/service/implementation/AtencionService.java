package com.medico.backend.service.implementation;

import com.medico.backend.dto.request.AtencionRequest;
import com.medico.backend.dto.request.DetalleRecetaDTO;
import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.clinical.*;
import com.medico.backend.model.core.Persona;
import com.medico.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtencionService {

    private final AtencionRepository atencionRepo;
    private final HistoriaClinicaRepository historiaRepo;
    private final CitaRepository citaRepo;
    private final Cie10Repository cie10Repo;
    private final SignoVitalRepository signoRepo;

    private final RecetaRepository recetaRepo;
    private final DetalleRecetaRepository detRecetaRepo;

    @Transactional
    public Atencion registrarAtencion(AtencionRequest req) {
        // 1. Validar Cita
        Cita cita = citaRepo.findById(req.getIdCita())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if ("FINALIZADO".equals(cita.getEstado())) {
            throw new RuntimeException("Esta cita ya fue atendida.");
        }

        // 2. Obtener o Crear Historia Clínica
        Persona paciente = cita.getPaciente();
        HistoriaClinica historia = historiaRepo.findByPacienteId(paciente.getIdPersona())
                .orElseGet(() -> {
                    HistoriaClinica nuevaHC = new HistoriaClinica();
                    nuevaHC.setPaciente(paciente);
                    return historiaRepo.save(nuevaHC);
                });

        // 3. Crear Atención
        Atencion atencion = new Atencion();
        atencion.setCita(cita);
        atencion.setHistoriaClinica(historia);
        atencion.setMedico(cita.getMedico());
        atencion.setFechaAtencion(LocalDateTime.now());

        atencion.setMotivoConsulta(req.getMotivoConsulta());
        atencion.setExploracionFisica(req.getExploracionFisica());
        atencion.setDiagnosticoPresuntivo(req.getDiagnosticoPresuntivo());
        atencion.setDiagnosticoDefinitivo(req.getDiagnosticoDefinitivo());
        atencion.setPlanTratamiento(req.getPlanTratamiento());
        atencion.setPronostico(req.getPronostico());

        if (req.getCodigoCie10() != null) {
            Cie10 cie10 = cie10Repo.findById(req.getCodigoCie10()).orElse(null);
            atencion.setCie10(cie10);
        }

        Atencion atencionGuardada = atencionRepo.save(atencion);

        // 4. Guardar Signos Vitales
        SignoVital signos = new SignoVital();
        signos.setAtencion(atencionGuardada);
        signos.setPeso(req.getPeso());
        signos.setTalla(req.getTalla());
        signos.setTemperatura(req.getTemperatura());
        signos.setFrecuenciaCardiaca(req.getFrecuenciaCardiaca());
        signos.setFrecuenciaRespiratoria(req.getFrecuenciaRespiratoria());
        signos.setSaturacionOxigeno(req.getSaturacionOxigeno());
        signos.setPresionArterial(req.getPresionArterial());

        if (req.getPeso() != null && req.getTalla() != null && req.getTalla().doubleValue() > 0) {
            BigDecimal tallaM = req.getTalla();
            BigDecimal imc = req.getPeso().divide(tallaM.multiply(tallaM), 2, RoundingMode.HALF_UP);
            signos.setImc(imc);
        }

        signoRepo.save(signos);

        // ✅ FIX: Asignar signos a la respuesta para que no salga null en el JSON
        atencionGuardada.setSignosVitales(List.of(signos));

        // 5. GENERAR RECETA (Nueva Lógica)
        if (req.getMedicamentos() != null && !req.getMedicamentos().isEmpty()) {
            Receta receta = new Receta();
            receta.setAtencion(atencionGuardada);
            receta.setFechaEmision(LocalDateTime.now());
            receta.setVigenciaDias(30);
            receta.setIndicacionesGenerales(req.getPlanTratamiento()); // Opcional: copiar el plan aquí

            Receta recetaGuardada = recetaRepo.save(receta);

            for (DetalleRecetaDTO det : req.getMedicamentos()) {
                DetalleReceta item = new DetalleReceta();
                item.setReceta(recetaGuardada);
                item.setMedicamentoNombre(det.getMedicamento());
                item.setDosis(det.getDosis());
                item.setFrecuencia(det.getFrecuencia());
                item.setDuracion(det.getDuracion());

                detRecetaRepo.save(item);
            }
        }

        // 6. Finalizar Cita
        cita.setEstado("FINALIZADO");
        citaRepo.save(cita);

        return atencionGuardada;
    }

    // Método para ver historial
    public List<Atencion> obtenerHistorialPaciente(Integer idPaciente) {
        HistoriaClinica hc = historiaRepo.findByPacienteId(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente sin historia clínica"));
        return atencionRepo.findByHistoriaClinicaIdHistoriaOrderByFechaAtencionDesc(hc.getIdHistoria());
    }
}