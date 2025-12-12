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
        // 1. Obtener usuario autenticado (Quien paga/agenda)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioTitular = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Definir quién es el PACIENTE
        Persona paciente;

        if (request.isEsParaTercero()) {
            // LÓGICA DE TERCERO:
            // Buscamos si ya existe una persona con ese DNI, si no, la creamos.
            paciente = personaRepository.findByNumeroDocumento(request.getPacienteDni())
                    .orElseGet(() -> {
                        // Crear nueva persona (sin usuario asociado, solo ficha médica)
                        Persona nuevaPersona = Persona.builder()
                                .nombres(request.getPacienteNombre())
                                .apellidoPaterno(request.getPacienteApellido()) // Asegúrate de tener este campo en Persona
                                .tipoDocumento("DNI")
                                .numeroDocumento(request.getPacienteDni())
                                .telefonoMovil(request.getPacienteTelefono())
                                // .email(request.getPacienteEmail()) // Si tienes email en Persona, agrégalo
                                .build();
                        return personaRepository.save(nuevaPersona);
                    });
        } else {
            // LÓGICA TITULAR:
            // El paciente es el mismo usuario logueado
            paciente = personaRepository.findByUsuario(usuarioTitular)
                    .orElseThrow(() -> new RuntimeException("El usuario titular no tiene perfil de persona creado."));
        }

        // 3. Buscar Médico y Modalidad
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        ModalidadCita modalidad = modalidadCitaRepository.findById(request.getModalidadId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada (ID: " + request.getModalidadId() + ")"));

        // 4. Validar Disponibilidad
        if (citaRepository.existsByMedicoAndFechaHoraInicio(medico, request.getFechaHora())) {
            throw new RuntimeException("El médico ya tiene una cita ocupada en ese horario.");
        }

        // 5. Buscar Tarifa
        Tarifa tarifa = tarifaService.buscarTarifaActiva(medico.getEspecialidad(), modalidad)
                .orElseThrow(() -> new RuntimeException("No hay tarifa configurada para esta especialidad/modalidad."));

        // 6. Crear Cita
        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .especialidad(medico.getEspecialidad())
                .modalidad(modalidad)
                .tarifa(tarifa)
                .fechaHoraInicio(request.getFechaHora())
                .fechaHoraFin(request.getFechaHora().plusMinutes(30)) // Citas de 30 min por defecto
                .estado("PENDIENTE")
                .motivoConsultaPaciente(request.getMotivoConsulta())

                // --- MAPEO DE DIRECCIÓN ---
                // Solo guardamos esto si es Domicilio (ID 2), si no, se guarda nulo para no ensuciar la BD
                .distrito(request.getModalidadId() == 2 ? request.getDistrito() : null)
                .direccionExacta(request.getModalidadId() == 2 ? request.getDireccionExacta() : null)
                .referencia(request.getModalidadId() == 2 ? request.getReferencia() : null)

                .origenReserva("WEB")
                .build();

        Cita citaGuardada = citaRepository.save(cita);

        // 7. Generar Orden de Pago
        ordenPagoService.generarOrdenPagoParaCita(citaGuardada, "PAGO_EN_CLINICA"); // Ojo: Aquí podrías usar request.getTipoPago() si lo envías

        // 8. Enviar Correo
        try {
            // Enviamos correo al email que puso en el formulario (si es tercero) o al del titular
            String emailNotificacion = request.isEsParaTercero() ? request.getPacienteEmail() : usuarioTitular.getEmail();

            emailService.enviarConfirmacionCita(
                    emailNotificacion,
                    paciente.getNombres(),
                    citaGuardada.getFechaHoraInicio().toString(),
                    citaGuardada.getMedico().getPersona().getApellidoPaterno()
            );
        } catch (Exception e) {
            log.error("Error enviando correo: {}", e.getMessage());
        }

        // Retornar respuesta
        // Nota: Asegúrate de refrescar la entidad para traer relaciones completas
        return mapToResponse(citaGuardada);
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