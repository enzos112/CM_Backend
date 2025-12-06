package com.medico.backend.service.implementation;

import com.medico.backend.dto.request.CitaRequest;
import com.medico.backend.dto.response.CitaResponse;
import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.administrative.OrdenPago;
import com.medico.backend.model.core.Persona;
import com.medico.backend.model.core.Usuario;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.model.infrastructure.ModalidadCita;
import com.medico.backend.model.infrastructure.Tarifa;
import com.medico.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService extends GenericService<Cita, Integer> {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final ModalidadCitaRepository modalidadCitaRepository;
    private final TarifaService tarifaService;
    private final OrdenPagoService ordenPagoService;

    @Override
    protected IGenericRepository<Cita, Integer> getRepo() {
        return citaRepository;
    }

    @Transactional
    public CitaResponse agendarCita(CitaRequest request) {
        // 1. Obtener usuario autenticado
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Persona paciente = personaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene perfil de paciente (Persona) asociado"));

        // 2. Buscar Médico
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        // 3. Buscar Modalidad (Por ID es más seguro, ajusta tu Request si puedes)
        ModalidadCita modalidad = modalidadCitaRepository.findById(request.getModalidadId())
                .orElseThrow(() -> new RuntimeException("Modalidad no encontrada"));

        Tarifa tarifa = tarifaService.buscarTarifaActiva(medico.getEspecialidad(), modalidad)
                .orElseThrow(() -> new RuntimeException("No se encontró una tarifa activa para la especialidad y modalidad seleccionadas."));

        // 4. Validar Disponibilidad
        boolean ocupado = citaRepository.existsByMedicoAndFechaHoraInicio(medico, request.getFechaHora());
        if (ocupado) {
            throw new RuntimeException("El médico ya tiene una cita en ese horario.");
        }

        // 5. Crear Cita
        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .especialidad(medico.getEspecialidad()) // Heredamos la especialidad del médico
                .modalidad(modalidad)
                .tarifa(tarifa)
                .fechaHoraInicio(request.getFechaHora())
                .fechaHoraFin(request.getFechaHora().plusMinutes(30)) // Duración default
                .estado("PENDIENTE")
                .motivoConsultaPaciente(request.getMotivoConsulta())
                .origenReserva("WEB")
                .build();

        Cita citaGuardada = citaRepository.save(cita);

        // --- 5. Generar Orden de Pago (Transacción 2: Módulo Financiero) ---
        // Asumimos "Pago en Clínica" (PENDIENTE) por defecto, basado en la Regla de Negocio
        OrdenPago orden = ordenPagoService.generarOrdenPagoParaCita(citaGuardada, "PAGO_EN_CLINICA");

        // OPCIONAL: Si el pago es anticipado (Modalidad VIRTUAL), el estado de la Cita cambia
        if (modalidad.getNombre().equals("VIRTUAL")) {
            // Aquí iría la lógica para procesar el pago online
            // Por ahora, solo actualizamos el estado de la orden para reflejar el requisito de pago anticipado
            // La lógica real de pasarela de pago actualizaría el estado a PAGADO
            // ordenPagoService.actualizarEstadoDePago(orden.getIdOrden(), "REQUERIDO_ONLINE");
        }

        // 6. Respuesta Mapeada
        return mapToResponse(citaGuardada);
    }

    public List<CitaResponse> listarMisCitas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Persona paciente = personaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de paciente no encontrado"));

        List<Cita> citas = citaRepository.findByPaciente(paciente);

        return citas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear entidad a DTO response
    private CitaResponse mapToResponse(Cita c) {
        return CitaResponse.builder()
                .id(c.getIdCita().longValue())
                .fechaHora(c.getFechaHoraInicio())
                .modalidad(c.getModalidad().getNombre())
                .estado(c.getEstado())
                .nombreMedico(c.getMedico().getPersona().getNombres() + " " + c.getMedico().getPersona().getApellidoPaterno())
                .especialidad(c.getEspecialidad() != null ? c.getEspecialidad().getNombre() : "General")
                .nombrePaciente(c.getPaciente().getNombres())
                .dniPaciente(c.getPaciente().getNumeroDocumento())
                .linkReunion(c.getLinkReunion())
                .build();
    }
}