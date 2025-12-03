package com.medico.backend.service;

import com.medico.backend.dto.request.CitaRequest;
import com.medico.backend.dto.response.CitaResponse;
import com.medico.backend.exception.ModelNotFoundException;
import com.medico.backend.model.Cita;
import com.medico.backend.model.Medico;
import com.medico.backend.model.Usuario;
import com.medico.backend.repository.CitaRepository;
import com.medico.backend.repository.MedicoRepository;
import com.medico.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public CitaResponse agendarCita(CitaRequest request) { // <--- CAMBIO: Devuelve CitaResponse
        // 1. Obtener usuario autenticado (desde el Token JWT)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario paciente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado"));

        // 2. Buscar Médico
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new ModelNotFoundException("Médico no encontrado"));

        // 3. VALIDACIÓN CRÍTICA: ¿El médico está libre?
        boolean ocupado = citaRepository.existsByMedicoAndFechaHora(request.getMedicoId(), request.getFechaHora());
        if (ocupado) {
            throw new IllegalArgumentException("El médico no está disponible en ese horario.");
        }

        // 4. Mapear DTO a Entidad
        Cita cita = new Cita();
        cita.setUsuarioPaciente(paciente);
        cita.setMedico(medico);
        cita.setFechaHora(request.getFechaHora());
        cita.setModalidad(request.getModalidad());
        cita.setEstado("PROGRAMADA");
        cita.setMotivoConsulta(request.getMotivoConsulta());

        // Datos adicionales (Triage)
        cita.setPeso(request.getPeso());
        cita.setAltura(request.getAltura());
        cita.setAlergias(request.getAlergias());
        cita.setAntecedentes(request.getAntecedentes());

        // Lógica "Para Tercero"
        cita.setEsParaTercero(request.isEsParaTercero());
        if (request.isEsParaTercero()) {
            cita.setPacienteNombre(request.getPacienteNombre());
            cita.setPacienteDni(request.getPacienteDni());
            cita.setPacienteTelefono(request.getPacienteTelefono());
        } else {
            // Si es para mí, copiamos los datos del usuario logueado por seguridad
            cita.setPacienteNombre(paciente.getNombres() + " " + paciente.getApellidoPaterno());
            cita.setPacienteDni(paciente.getNumeroDocumento());
            cita.setPacienteTelefono(paciente.getTelefono());
        }

        // 5. Guardar y Convertir a Response DTO
        Cita citaGuardada = citaRepository.save(cita);

        return CitaResponse.builder()
                .id(citaGuardada.getId())
                .fechaHora(citaGuardada.getFechaHora())
                .modalidad(citaGuardada.getModalidad().name())
                .estado(citaGuardada.getEstado())
                // Construimos el nombre completo del médico
                .nombreMedico(medico.getUsuario().getNombres() + " " + medico.getUsuario().getApellidoPaterno())
                .especialidad(medico.getEspecialidad().getNombre())
                // Usamos los datos que ya guardamos en la cita (ya sea propios o de tercero)
                .nombrePaciente(citaGuardada.getPacienteNombre())
                .dniPaciente(citaGuardada.getPacienteDni())
                .linkReunion(citaGuardada.getLinkReunion())
                .build();
    }

    // Método auxiliar para listar citas (puedes dejarlo pendiente o actualizarlo igual)
    public List<Cita> listarMisCitas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario paciente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ModelNotFoundException("Usuario no encontrado"));

        return citaRepository.findByUsuarioPacienteId(paciente.getId());
    }
}