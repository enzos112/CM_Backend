package com.medico.backend.service.implementation;

import com.medico.backend.dto.request.CitaRequest;
import com.medico.backend.dto.response.CitaResponse;
import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.administrative.OrdenPago;
import com.medico.backend.model.administrative.SolicitudCancelacion;
import com.medico.backend.model.core.Persona;
import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.model.infrastructure.ModalidadCita;
import com.medico.backend.model.infrastructure.Tarifa;
import com.medico.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Importante para los logs
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Activa la variable 'log'
public class CitaService extends GenericService<Cita, Integer> {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final ModalidadCitaRepository modalidadCitaRepository;
    private final TarifaService tarifaService;
    private final OrdenPagoService ordenPagoService;
    private final SolicitudCancelacionRepository solicitudRepo;
    private final EmailService emailService;

    @Override
    protected IGenericRepository<Cita, Integer> getRepo() {
        return citaRepository;
    }

    @Transactional
    public CitaResponse agendarCita(CitaRequest request) {
        // 1. Obtener usuario
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Persona paciente = personaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene perfil de paciente"));

        // 2. Buscar Médico y Modalidad
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        ModalidadCita modalidad = modalidadCitaRepository.findById(request.getModalidadId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        // 3. Tarifa y Disponibilidad
        Tarifa tarifa = tarifaService.buscarTarifaActiva(medico.getEspecialidad(), modalidad)
                .orElseThrow(() -> new RuntimeException("No hay tarifa activa."));

        if (citaRepository.existsByMedicoAndFechaHoraInicio(medico, request.getFechaHora())) {
            throw new RuntimeException("El médico ya tiene una cita en ese horario.");
        }

        // 4. Crear Cita
        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .especialidad(medico.getEspecialidad())
                .modalidad(modalidad)
                .tarifa(tarifa)
                .fechaHoraInicio(request.getFechaHora())
                .fechaHoraFin(request.getFechaHora().plusMinutes(30))
                .estado("PENDIENTE")
                .motivoConsultaPaciente(request.getMotivoConsulta())
                .origenReserva("WEB")
                .build();

        Cita citaGuardada = citaRepository.save(cita);

        // 5. Generar Orden
        ordenPagoService.generarOrdenPagoParaCita(citaGuardada, "PAGO_EN_CLINICA");

        // --- INTEGRACIÓN DE CORREO CON LOGS ---
        try {
            emailService.enviarConfirmacionCita(
                    citaGuardada.getPaciente().getUsuario().getEmail(),
                    citaGuardada.getPaciente().getNombres(),
                    citaGuardada.getFechaHoraInicio().toString(),
                    citaGuardada.getMedico().getPersona().getApellidoPaterno()
            );
            log.info("📧 Correo enviado exitosamente a: {}", citaGuardada.getPaciente().getUsuario().getEmail());
        } catch (Exception e) {
            log.error("⚠️ Falló el envío de correo para la cita {}: {}", citaGuardada.getCodigo(), e.getMessage());
            // No lanzamos error para no romper la transacción de la cita
        }

        Cita citaActualizada = citaRepository.findById(citaGuardada.getIdCita()).orElse(citaGuardada);
        return mapToResponse(citaActualizada);
    }

    public List<CitaResponse> listarMisCitas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Persona paciente = personaRepository.findByUsuario(usuario).orElseThrow(() -> new RuntimeException("Perfil de paciente no encontrado"));
        List<Cita> citas = citaRepository.findByPaciente(paciente);
        return citas.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<CitaResponse> listarAgendaMedico(LocalDate fecha) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Medico medico = medicoRepository.findByUsuario(usuario).orElseThrow(() -> new RuntimeException("No eres médico."));

        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();
        LocalDateTime inicioDia = fechaConsulta.atStartOfDay();
        LocalDateTime finDia = fechaConsulta.atTime(LocalTime.MAX);

        List<Cita> agenda = citaRepository.findByMedicoAndFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(medico, inicioDia, finDia);
        return agenda.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- NUEVA LÓGICA DE CANCELACIÓN (SOLICITUDES) ---

    @Transactional
    public void solicitarCancelacion(Integer idCita, String motivo, String evidenciaUrl) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Cita cita = citaRepository.findById(idCita).orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getPaciente().getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("Solo el paciente titular puede solicitar la cancelación.");
        }
        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException("Solo se pueden solicitar cancelaciones de citas PENDIENTES.");
        }

        SolicitudCancelacion solicitud = SolicitudCancelacion.builder()
                .cita(cita)
                .usuarioSolicitante(usuario)
                .motivo(motivo)
                .evidenciaUrl(evidenciaUrl)
                .build();

        solicitudRepo.save(solicitud);

        cita.setEstado("EN_EVALUACION");
        citaRepository.save(cita);

        log.info("📝 Solicitud de cancelación creada para la cita: {}", cita.getCodigo());
    }

    @Transactional
    public void evaluarSolicitud(Integer idSolicitud, boolean aprobado, String respuestaAdmin) {
        SolicitudCancelacion solicitud = solicitudRepo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        Cita cita = solicitud.getCita();

        solicitud.setRespuestaAdmin(respuestaAdmin);

        if (aprobado) {
            solicitud.setEstadoSolicitud("APROBADA");
            cita.setEstado("CANCELADO");

            if (cita.getDetalleOrden() != null && cita.getDetalleOrden().getOrdenPago() != null) {
                OrdenPago orden = cita.getDetalleOrden().getOrdenPago();
                if ("PAGADO".equals(orden.getEstado())) {
                    orden.setEstado("REEMBOLSO_PENDIENTE");
                } else {
                    orden.setEstado("ANULADO");
                }
            }
            log.info("✅ Solicitud APROBADA. Cita {} cancelada.", cita.getCodigo());
        } else {
            solicitud.setEstadoSolicitud("RECHAZADA");
            cita.setEstado("PENDIENTE");
            log.info("❌ Solicitud RECHAZADA. Cita {} restaurada a PENDIENTE.", cita.getCodigo());
        }

        solicitudRepo.save(solicitud);
        citaRepository.save(cita);
    }

    public List<SolicitudCancelacion> listarSolicitudesPendientes() {
        return solicitudRepo.findByEstadoSolicitud("PENDIENTE");
    }

    // --- MAPPER ---
    private CitaResponse mapToResponse(Cita c) {
        String estadoPago = "SIN_ORDEN";
        BigDecimal precio = BigDecimal.ZERO;
        if(c.getDetalleOrden() != null && c.getDetalleOrden().getOrdenPago() != null) {
            estadoPago = c.getDetalleOrden().getOrdenPago().getEstado();
            precio = c.getDetalleOrden().getOrdenPago().getMontoTotal();
        } else if (c.getTarifa() != null) {
            precio = c.getTarifa().getPrecio();
        }
        return CitaResponse.builder()
                .id(c.getIdCita().longValue())
                .fechaHora(c.getFechaHoraInicio())
                .modalidad(c.getModalidad().getNombre())
                .estado(c.getEstado())
                .nombreMedico(c.getMedico().getPersona().getNombres() + " " + c.getMedico().getPersona().getApellidoPaterno())
                .especialidad(c.getEspecialidad() != null ? c.getEspecialidad().getNombre() : "General")
                .nombrePaciente(c.getPaciente().getNombres() + " " + c.getPaciente().getApellidoPaterno())
                .dniPaciente(c.getPaciente().getNumeroDocumento())
                .motivoConsulta(c.getMotivoConsultaPaciente())
                .linkReunion(c.getLinkReunion())
                .precio(precio)
                .estadoPago(estadoPago)
                .build();
    }
}