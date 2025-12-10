package com.medico.backend.controller;

import com.medico.backend.dto.response.PacienteAtencionDTO;
import com.medico.backend.model.administrative.Cita;
import com.medico.backend.model.clinical.Atencion;
import com.medico.backend.model.clinical.DetalleReceta;
import com.medico.backend.model.clinical.Receta;
import com.medico.backend.model.core.Persona;
import com.medico.backend.repository.AtencionRepository;
import com.medico.backend.repository.CitaRepository;
import com.medico.backend.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/web")
@RequiredArgsConstructor
public class PerfilPacienteController {

    private final PersonaRepository personaRepository;
    private final CitaRepository citaRepository;
    private final AtencionRepository atencionRepository; // Inyección nueva necesaria

    @PutMapping("/perfil")
    public Persona actualizarPerfil(Authentication auth, @RequestBody Persona datos) {
        String email = auth.getName();
        Persona persona = personaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        persona.setTelefonoMovil(datos.getTelefonoMovil());
        persona.setDireccionCalle(datos.getDireccionCalle());
        // Puedes agregar más campos aquí si es necesario (distrito, etc.)

        return personaRepository.save(persona);
    }

    @GetMapping("/historial")
    public List<Cita> historial(Authentication auth) {
        String email = auth.getName();
        return citaRepository.findByPacienteUsuarioEmail(email);
    }

    // --- NUEVO ENDPOINT: VER DETALLE DE ATENCIÓN (RESULTADOS) ---
    @GetMapping("/atencion/{idCita}")
    public ResponseEntity<PacienteAtencionDTO> verDetalleAtencion(Authentication auth, @PathVariable Integer idCita) {
        String email = auth.getName();

        // 1. Buscar la Atención médica asociada a la Cita
        Atencion atencion = atencionRepository.findByCitaIdCita(idCita)
                .orElseThrow(() -> new RuntimeException("Aún no hay registros médicos para esta cita."));

        // 2. SEGURIDAD: Verificar que la cita pertenezca al usuario logueado
        // Navegamos: Atencion -> Cita -> Paciente -> Usuario -> Email
        String emailPaciente = atencion.getCita().getPaciente().getUsuario().getEmail();
        if (!emailPaciente.equals(email)) {
            throw new RuntimeException("Acceso denegado: No tienes permiso para ver esta historia clínica.");
        }

        // 3. Mapear Receta (Medicamentos)
        // Extraemos los medicamentos de la lista de recetas de la atención
        List<PacienteAtencionDTO.MedicamentoDTO> listaMedicamentos = new ArrayList<>();

        if (atencion.getRecetas() != null) {
            for (Receta r : atencion.getRecetas()) {
                if (r.getDetalles() != null) {
                    for (DetalleReceta d : r.getDetalles()) {
                        listaMedicamentos.add(PacienteAtencionDTO.MedicamentoDTO.builder()
                                .medicina(d.getMedicamentoNombre())
                                .dosis(d.getDosis())
                                .indicaciones(d.getFrecuencia() + " - " + d.getDuracion())
                                .build());
                    }
                }
            }
        }

        // 4. Construir respuesta final (DTO)
        PacienteAtencionDTO response = PacienteAtencionDTO.builder()
                .idAtencion(atencion.getIdAtencion().longValue())
                .fecha(atencion.getFechaAtencion())
                .medico(atencion.getMedico().getPersona().getNombres() + " " + atencion.getMedico().getPersona().getApellidoPaterno())
                .especialidad(atencion.getMedico().getEspecialidad().getNombre())
                .motivoConsulta(atencion.getMotivoConsulta())
                .diagnostico(atencion.getDiagnosticoDefinitivo())
                .tratamiento(atencion.getPlanTratamiento())
                .receta(listaMedicamentos)
                .build();

        return ResponseEntity.ok(response);
    }
}